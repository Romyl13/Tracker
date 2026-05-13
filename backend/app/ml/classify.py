"""
Minimal scikit-learn relapse pattern classifier.

Trains a small in-memory model on synthetic data at first use (or loads from ML_MODEL_PATH).
"""

from __future__ import annotations

import os
from dataclasses import dataclass
from threading import Lock

import joblib
import numpy as np
from scipy.sparse import csr_matrix, hstack
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression

from app.core.config import settings

_MODEL_LOCK = Lock()
_MODEL: "_RelapseModel" | None = None

LABELS = [
    "stress_triggered",
    "social_situation",
    "boredom_or_habit",
    "fatigue_or_sleep",
    "unknown_pattern",
]


@dataclass
class RelapseFeatures:
    time_of_day_bucket: str
    stress_level: int
    reason_text: str


class _RelapseModel:
    def __init__(self) -> None:
        self._vec = TfidfVectorizer(max_features=120, ngram_range=(1, 2))
        self._clf = LogisticRegression(max_iter=400, random_state=42)

    def fit(self, texts: list[str], stress: list[float], y: np.ndarray) -> None:
        x_text = self._vec.fit_transform(texts)
        x_num = csr_matrix(np.asarray(stress, dtype=float).reshape(-1, 1))
        x = hstack([x_text, x_num])
        self._clf.fit(x, y)

    def predict_label_idx(self, text: str, stress: float) -> int:
        x_text = self._vec.transform([text])
        x_num = csr_matrix([[float(stress)]])
        x = hstack([x_text, x_num])
        return int(self._clf.predict(x)[0])


def _synthetic_corpus():
    rows: list[tuple[str, float, int]] = []
    templates = [
        ("stress anxiety panic deadline overwhelmed", 8.5, 0),
        ("friends party bar celebration social", 4.0, 1),
        ("bored nothing habit automatic idle", 3.0, 2),
        ("tired exhausted sleep deprivation night shift", 7.0, 3),
        ("unclear random mixed", 5.0, 4),
    ]
    buckets = ["morning", "afternoon", "evening", "night"]
    for base, stress, lab in templates:
        for b in buckets:
            for _ in range(25):
                text = f"{b} {base}"
                rows.append((text, float(stress + np.random.uniform(-1, 1)), lab))
    texts, stress, y = zip(*rows, strict=True)
    return list(texts), list(stress), np.array(y)


def _build_model() -> _RelapseModel:
    m = _RelapseModel()
    texts, stress, y = _synthetic_corpus()
    m.fit(texts, stress, y)
    return m


def get_pipeline() -> _RelapseModel:
    """Name kept for parity with service docs; returns the sklearn-backed model wrapper."""
    global _MODEL
    with _MODEL_LOCK:
        if _MODEL is not None:
            return _MODEL
        path = settings.ml_model_path or os.environ.get("ML_MODEL_PATH")
        if path and os.path.isfile(path):
            _MODEL = joblib.load(path)
            return _MODEL
        _MODEL = _build_model()
        return _MODEL


def classify_relapse(features: RelapseFeatures) -> tuple[str, str]:
    """
    Returns (machine_label, human_readable_explanation).
    """
    model = get_pipeline()
    text = f"{features.time_of_day_bucket} {features.reason_text}"
    stress = float(np.clip(features.stress_level, 1, 10))
    idx = model.predict_label_idx(text, stress)
    idx = min(max(idx, 0), len(LABELS) - 1)
    label = LABELS[idx]
    human = {
        "stress_triggered": "Relapse pattern: stress / emotional pressure",
        "social_situation": "Relapse pattern: social context",
        "boredom_or_habit": "Relapse pattern: boredom or automatic habit",
        "fatigue_or_sleep": "Relapse pattern: fatigue or poor sleep",
        "unknown_pattern": "Relapse pattern: mixed / unclear drivers",
    }[label]
    return label, human
