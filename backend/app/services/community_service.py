"""Community feed: create posts and upvote."""

from __future__ import annotations

import uuid
from datetime import datetime, timezone

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.community_post import CommunityPost
from app.models.user import User
from app.schemas import CommunityPostCreateIn, CommunityPostOut


async def list_posts(db: AsyncSession) -> list[CommunityPostOut]:
    q = select(CommunityPost).order_by(CommunityPost.created_at.desc()).limit(100)
    rows = (await db.execute(q)).scalars().all()
    out: list[CommunityPostOut] = []
    for p in rows:
        author = await db.get(User, p.author_id)
        out.append(
            CommunityPostOut(
                id=p.id,
                author_display_name=author.display_name if author else None,
                title=p.title,
                body=p.body,
                upvotes=p.upvotes,
                created_at=p.created_at,
            )
        )
    return out


async def create_post(db: AsyncSession, author: User, body: CommunityPostCreateIn) -> CommunityPostOut:
    post = CommunityPost(
        id=uuid.uuid4(),
        author_id=author.id,
        title=body.title,
        body=body.body,
        upvotes=0,
        created_at=datetime.now(timezone.utc),
    )
    db.add(post)
    await db.flush()
    return CommunityPostOut(
        id=post.id,
        author_display_name=author.display_name,
        title=post.title,
        body=post.body,
        upvotes=post.upvotes,
        created_at=post.created_at,
    )


async def upvote(db: AsyncSession, post_id: uuid.UUID) -> CommunityPostOut | None:
    post = await db.get(CommunityPost, post_id)
    if not post:
        return None
    post.upvotes += 1
    await db.flush()
    author = await db.get(User, post.author_id)
    return CommunityPostOut(
        id=post.id,
        author_display_name=author.display_name if author else None,
        title=post.title,
        body=post.body,
        upvotes=post.upvotes,
        created_at=post.created_at,
    )
