"""Business logic: user provisioning from Firebase token subject."""

from __future__ import annotations

import uuid

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.user import User
from app.schemas import UserOut, UserSyncIn


async def get_or_create_user(db: AsyncSession, firebase_uid: str, payload: UserSyncIn | None) -> User:
    result = await db.execute(select(User).where(User.firebase_uid == firebase_uid))
    user = result.scalar_one_or_none()
    if user:
        if payload:
            if payload.email is not None:
                user.email = payload.email
            if payload.display_name is not None:
                user.display_name = payload.display_name
            if payload.daily_cost_uah is not None:
                user.daily_cost_uah = payload.daily_cost_uah
        return user

    user = User(
        id=uuid.uuid4(),
        firebase_uid=firebase_uid,
        email=payload.email if payload else None,
        display_name=payload.display_name if payload else None,
        daily_cost_uah=payload.daily_cost_uah if payload and payload.daily_cost_uah is not None else 0.0,
    )
    db.add(user)
    await db.flush()
    return user


def to_user_out(user: User) -> UserOut:
    return UserOut.model_validate(user)
