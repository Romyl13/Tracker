"""User entity stored in PostgreSQL (synced from Firebase on first API call)."""

from __future__ import annotations

import uuid
from datetime import datetime

from sqlalchemy import DateTime, String
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db.base import Base


class User(Base):
    __tablename__ = "users"

    id: Mapped[uuid.UUID] = mapped_column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    firebase_uid: Mapped[str] = mapped_column(String(128), unique=True, index=True)
    email: Mapped[str | None] = mapped_column(String(255), nullable=True)
    display_name: Mapped[str | None] = mapped_column(String(255), nullable=True)
    streak_days: Mapped[int] = mapped_column(default=0)
    total_xp: Mapped[int] = mapped_column(default=0)
    daily_cost_uah: Mapped[float] = mapped_column(default=0.0)
    created_at: Mapped[datetime] = mapped_column(DateTime(timezone=True), default=datetime.utcnow)

    habit_logs: Mapped[list["HabitLog"]] = relationship("HabitLog", back_populates="user", cascade="all, delete-orphan")
    posts: Mapped[list["CommunityPost"]] = relationship("CommunityPost", back_populates="author", cascade="all, delete-orphan")
    insights: Mapped[list["AnalysisInsight"]] = relationship("AnalysisInsight", back_populates="user", cascade="all, delete-orphan")
