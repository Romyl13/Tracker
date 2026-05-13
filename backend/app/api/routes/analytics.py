"""Aggregated analytics for the Analysis screen."""

from fastapi import APIRouter, Depends
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.security import get_current_user_id
from app.db.session import get_db
from app.schemas import AnalyticsSummaryOut
from app.services import habit_service, user_service

router = APIRouter(prefix="/analytics", tags=["analytics"])


@router.get("/summary", response_model=AnalyticsSummaryOut)
async def summary(
    db: AsyncSession = Depends(get_db),
    firebase_uid: str = Depends(get_current_user_id),
):
    user = await user_service.get_or_create_user(db, firebase_uid, None)
    await db.commit()
    return await habit_service.build_analytics(db, user)
