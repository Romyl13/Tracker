# Tracker
Native Android application for habit tracking, gamification, and relapse prediction using Machine Learning.

## Overview
BreakFree AI is a client-server system designed to assist users in overcoming bad habits. Unlike standard trackers, it utilizes a **Random Forest** classification model to analyze user behavior patterns (time, location, stress levels) and predict the probability of relapse in real-time.

The system features a centralized Python backend, a native Kotlin Android client, and a persistent PostgreSQL database.

## Key Features
* **Predictive Analytics:** AI module that estimates relapse risk based on historical data.
* **Offline-First Architecture:** Local data persistence using Room DB with background synchronization.
* **Social Interaction:** Anonymous community feed for support (REST API integration).
* **Gamification:** Leveling system and achievement tracking.
* **Secure Auth:** OAuth 2.0 via Google Identity Services.

## Tech Stack

| Component | Technology |
| :--- | :--- |
| **Mobile Client** | Android SDK, Kotlin, Jetpack Compose |
| **Networking** | Retrofit, OkHttp, Coroutines |
| **Backend API** | Python, FastAPI, Pydantic |
| **Database** | PostgreSQL (Cloud), SQLite (Local) |
| **Machine Learning** | Scikit-learn, Pandas |
| **Design** | Material Design 3 |

## Project Structure
```text
breakfree-ai/
├── android-app/      # Android Studio project source code
├── backend-api/      # Python FastAPI server & endpoints
├── ai-engine/        # ML model training scripts & datasets
├── docs/             # Technical documentation & architecture diagrams
└── README.md         # Project overview
