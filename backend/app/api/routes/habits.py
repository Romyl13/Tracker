"""Habit logging: check-in, relapse (ML), history."""

from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.security import get_current_user_id
from app.db.session import get_db
from app.schemas import CheckInIn, HabitLogOut, RelapseIn
from app.services import habit_service, user_service

router = APIRouter(prefix="/habits", tags=["habits"])


@router.post("/check-in", response_model=HabitLogOut)
async def check_in(
    body: CheckInIn,
    db: AsyncSession = Depends(get_db),
    firebase_uid: str = Depends(get_current_user_id),
):
    user = await user_service.get_or_create_user(db, firebase_uid, None)
    log = await habit_service.record_check_in(db, user, body)
    await db.commit()
    return log


@router.post("/relapse", response_model=HabitLogOut)
async def relapse(
    body: RelapseIn,
    db: AsyncSession = Depends(get_db),
    firebase_uid: str = Depends(get_current_user_id),
):
    """
    Persists relapse context and invokes `app.ml.classify.classify_relapse` inside the service.
    """
    user = await user_service.get_or_create_user(db, firebase_uid, None)
    log = await habit_service.record_relapse(db, user, body)
    await db.commit()
    return log


@router.get("/logs", response_model=list[HabitLogOut])
async def logs(
    db: AsyncSession = Depends(get_db),
    firebase_uid: str = Depends(get_current_user_id),
):
    user = await user_service.get_or_create_user(db, firebase_uid, None)
    await db.commit()
    return await habit_service.list_logs(db, user)
