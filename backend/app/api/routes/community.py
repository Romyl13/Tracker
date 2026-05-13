"""Community posts and reactions."""

import uuid

from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.security import get_current_user_id
from app.db.session import get_db
from app.schemas import CommunityPostCreateIn, CommunityPostOut
from app.services import community_service, user_service

router = APIRouter(prefix="/community", tags=["community"])


@router.get("/posts", response_model=list[CommunityPostOut])
async def posts(db: AsyncSession = Depends(get_db)):
    return await community_service.list_posts(db)


@router.post("/posts", response_model=CommunityPostOut)
async def create_post(
    body: CommunityPostCreateIn,
    db: AsyncSession = Depends(get_db),
    firebase_uid: str = Depends(get_current_user_id),
):
    author = await user_service.get_or_create_user(db, firebase_uid, None)
    post = await community_service.create_post(db, author, body)
    await db.commit()
    return post


@router.post("/posts/{post_id}/upvote", response_model=CommunityPostOut)
async def upvote_post(
    post_id: uuid.UUID,
    db: AsyncSession = Depends(get_db),
    firebase_uid: str = Depends(get_current_user_id),  # ensures authenticated client
):
    _ = firebase_uid
    updated = await community_service.upvote(db, post_id)
    if not updated:
        raise HTTPException(status_code=404, detail="Post not found")
    await db.commit()
    return updated
