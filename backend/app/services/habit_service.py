"""Habit check-ins, relapses (with ML classification), streak and XP rules."""

from __future__ import annotations

import uuid
from datetime import datetime, timezone

from sqlalchemy import func, select
from sqlalchemy.ext.asyncio import AsyncSession

from app.ml.classify import RelapseFeatures, classify_relapse
from app.models.analysis_insight import AnalysisInsight
from app.models.habit_log import HabitEventType, HabitLog
from app.models.user import User
from app.schemas import (
    AnalysisInsightOut,
    AnalyticsSummaryOut,
    CheckInIn,
    HabitLogOut,
    RelapseIn,
)


CHECK_IN_XP = 25


async def record_check_in(db: AsyncSession, user: User, _: CheckInIn) -> HabitLogOut:
    user.streak_days += 1
    user.total_xp += CHECK_IN_XP
    log = HabitLog(
        id=uuid.uuid4(),
        user_id=user.id,
        event_type=HabitEventType.CHECK_IN,
        occurred_at=datetime.now(timezone.utc),
        xp_awarded=CHECK_IN_XP,
    )
    db.add(log)
    await db.flush()
    return HabitLogOut.model_validate(log)


async def record_relapse(db: AsyncSession, user: User, body: RelapseIn) -> HabitLogOut:
    ml_label, human = classify_relapse(
        RelapseFeatures(
            time_of_day_bucket=body.time_of_day_bucket,
            stress_level=body.stress_level,
            reason_text=body.reason_text,
        )
    )
    user.streak_days = 0
    log = HabitLog(
        id=uuid.uuid4(),
        user_id=user.id,
        event_type=HabitEventType.RELAPSE,
        occurred_at=datetime.now(timezone.utc),
        xp_awarded=0,
        time_of_day_bucket=body.time_of_day_bucket,
        stress_level=body.stress_level,
        reason_text=body.reason_text,
        ml_label=ml_label,
    )
    db.add(log)
    insight = AnalysisInsight(
        id=uuid.uuid4(),
        user_id=user.id,
        title="Relapse driver (ML)",
        detail=human,
        source="ml_relapse_classifier",
    )
    db.add(insight)
    await db.flush()
    return HabitLogOut.model_validate(log)


async def list_logs(db: AsyncSession, user: User, limit: int = 200) -> list[HabitLogOut]:
    q = (
        select(HabitLog)
        .where(HabitLog.user_id == user.id)
        .order_by(HabitLog.occurred_at.desc())
        .limit(limit)
    )
    rows = (await db.execute(q)).scalars().all()
    return [HabitLogOut.model_validate(r) for r in rows]


async def build_analytics(db: AsyncSession, user: User) -> AnalyticsSummaryOut:
    total = await db.scalar(select(func.count()).select_from(HabitLog).where(HabitLog.user_id == user.id)) or 0
    successes = (
        await db.scalar(
            select(func.count())
            .select_from(HabitLog)
            .where(HabitLog.user_id == user.id, HabitLog.event_type == HabitEventType.CHECK_IN)
        )
        or 0
    )
    relapses = (
        await db.scalar(
            select(func.count())
            .select_from(HabitLog)
            .where(HabitLog.user_id == user.id, HabitLog.event_type == HabitEventType.RELAPSE)
        )
        or 0
    )
    denom = successes + relapses
    ratio = float(successes) / float(denom) if denom else 1.0
    money_saved = float(user.daily_cost_uah) * float(user.streak_days)

    rel_hist = (
        (
            await db.execute(
                select(HabitLog)
                .where(HabitLog.user_id == user.id, HabitLog.event_type == HabitEventType.RELAPSE)
                .order_by(HabitLog.occurred_at.desc())
                .limit(50)
            )
        )
        .scalars()
        .all()
    )

    ins_rows = (
        (
            await db.execute(
                select(AnalysisInsight).where(AnalysisInsight.user_id == user.id).order_by(AnalysisInsight.created_at.desc()).limit(20)
            )
        )
        .scalars()
        .all()
    )

    return AnalyticsSummaryOut(
        streak_days=user.streak_days,
        total_xp=user.total_xp,
        successful_days_ratio=ratio,
        money_saved_uah=money_saved,
        relapse_history=[HabitLogOut.model_validate(x) for x in rel_hist],
        insights=[AnalysisInsightOut.model_validate(x) for x in ins_rows],
    )
