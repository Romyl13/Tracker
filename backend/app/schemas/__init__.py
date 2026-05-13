"""Pydantic request/response models shared by API routers."""

from __future__ import annotations

from datetime import datetime
from enum import Enum
from uuid import UUID

from pydantic import BaseModel, Field


class UserOut(BaseModel):
    id: UUID
    firebase_uid: str
    email: str | None
    display_name: str | None
    streak_days: int
    total_xp: int
    daily_cost_uah: float

    model_config = {"from_attributes": True}


class UserSyncIn(BaseModel):
    email: str | None = None
    display_name: str | None = None
    daily_cost_uah: float | None = Field(default=None, description="UAH saved per abstinent day for analytics")


class HabitEventType(str, Enum):
    CHECK_IN = "check_in"
    RELAPSE = "relapse"


class HabitLogOut(BaseModel):
    id: UUID
    user_id: UUID
    event_type: HabitEventType
    occurred_at: datetime
    xp_awarded: int
    time_of_day_bucket: str | None
    stress_level: int | None
    reason_text: str | None
    ml_label: str | None

    model_config = {"from_attributes": True}


class CheckInIn(BaseModel):
    """Successful day mark — optional metadata for future analytics."""

    note: str | None = None


class RelapseIn(BaseModel):
    time_of_day_bucket: str = Field(..., examples=["morning", "afternoon", "evening", "night"])
    stress_level: int = Field(..., ge=1, le=10)
    reason_text: str = Field(..., min_length=1, max_length=2000)


class CommunityPostOut(BaseModel):
    id: UUID
    author_display_name: str | None
    title: str
    body: str
    upvotes: int
    created_at: datetime


class CommunityPostCreateIn(BaseModel):
    title: str = Field(..., min_length=1, max_length=255)
    body: str = Field(..., min_length=1)


class AnalysisInsightOut(BaseModel):
    id: UUID
    title: str
    detail: str
    source: str
    created_at: datetime

    model_config = {"from_attributes": True}


class AnalyticsSummaryOut(BaseModel):
    streak_days: int
    total_xp: int
    successful_days_ratio: float
    money_saved_uah: float
    relapse_history: list[HabitLogOut]
    insights: list[AnalysisInsightOut]
