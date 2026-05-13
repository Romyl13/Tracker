"""User sync endpoint — maps Firebase identity to PostgreSQL profile."""

from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.security import get_current_user_id
from app.db.session import get_db
from app.schemas import UserOut, UserSyncIn
from app.services import user_service

router = APIRouter(prefix="/users", tags=["users"])


@router.post("/sync", response_model=UserOut)
async def sync_user(
    body: UserSyncIn | None = None,
    db: AsyncSession = Depends(get_db),
    firebase_uid: str = Depends(get_current_user_id),
):
    """
    Android: after Google Sign-In, call with `Authorization: Bearer <idToken>`.
    Creates or updates the server-side user row.
    """
    user = await user_service.get_or_create_user(db, firebase_uid, body)
    await db.commit()
    return user_service.to_user_out(user)


@router.get("/me", response_model=UserOut)
async def me(
    db: AsyncSession = Depends(get_db),
    firebase_uid: str = Depends(get_current_user_id),
):
    user = await user_service.get_or_create_user(db, firebase_uid, None)
    await db.commit()
    return user_service.to_user_out(user)
