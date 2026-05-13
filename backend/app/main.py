"""
Application entrypoint: wires routers, lifespan DB setup, and CORS for the Android client.
"""

from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app.api.routes import analytics, community, habits, users
from app.core.config import settings
from app.db.base import Base
from app.db.session import engine
from app.models import (  # noqa: F401 — register mappers with Base.metadata
    analysis_insight,
    community_post,
    habit_log,
    user,
)


@asynccontextmanager
async def lifespan(_: FastAPI):
    """Create database tables on startup (demo); use Alembic in production."""
    async with engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    yield
    await engine.dispose()


app = FastAPI(
    title="Health Tracker API",
    description="REST backend for habit tracking, community feed, and relapse analytics.",
    version="0.1.0",
    lifespan=lifespan,
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(users.router, prefix="/api/v1")
app.include_router(habits.router, prefix="/api/v1")
app.include_router(community.router, prefix="/api/v1")
app.include_router(analytics.router, prefix="/api/v1")


@app.get("/health")
async def health():
    return {"status": "ok"}
