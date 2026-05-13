"""Centralized settings loaded from environment variables."""

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="ignore")

    database_url: str = "postgresql+asyncpg://health_user:health_pass@localhost:5432/health_tracker"
    firebase_project_id: str = "demo-project"
    ml_model_path: str | None = None


settings = Settings()
