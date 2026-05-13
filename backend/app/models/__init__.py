"""Register ORM modules for import side effects."""

from app.models.analysis_insight import AnalysisInsight
from app.models.community_post import CommunityPost
from app.models.habit_log import HabitLog
from app.models.user import User

__all__ = ["User", "HabitLog", "CommunityPost", "AnalysisInsight"]
