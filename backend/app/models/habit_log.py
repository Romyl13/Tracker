"""Habit check-in and relapse events."""

from __future__ import annotations

import uuid
from datetime import datetime
from enum import Enum

from sqlalchemy import DateTime, Enum as PgEnum, ForeignKey, Integer, String, Text
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base


class HabitEventType(str, Enum):
    CHECK_IN = "check_in"
    RELAPSE = "relapse"


class HabitLog(Base):
    __tablename__ = "habit_logs"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    user_id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), ForeignKey("users.id"), index=True)
    event_type: Mapped[HabitEventType] = mapped_column(PgEnum(HabitEventType, name="habit_event_type"))
    occurred_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=datetime.utcnow, index=True)
    xp_awarded: Mapped[int] = mapped_column(Integer, default=0)

    # Relapse context (optional) — fed to ML module
    time_of_day_bucket: Mapped[str | None] = mapped_column(String(32), nullable=True)
    stress_level: Mapped[int | None] = mapped_column(Integer, nullable=True)
    reason_text: Mapped[str | None] = mapped_column(Text, nullable=True)
    ml_label: Mapped[str | None] = mapped_column(String(128), nullable=True)

    user: Mapped["User"] = relationship("User", back_populates="habit_logs")
