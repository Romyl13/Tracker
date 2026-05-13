# Health Tracker (Android + FastAPI + PostgreSQL + scikit-learn)

This repository contains a **reference N-tier implementation** for a minimalist abstinence / habit tracker:

- **Android client** (`android/`): Kotlin, Jetpack Compose (Material 3), Room, Retrofit, optional Firebase Auth.
- **API server** (`backend/`): FastAPI, SQLAlchemy 2.x (async), PostgreSQL, Pydantic v2.
- **ML module** (`backend/app/ml/classify.py`): scikit-learn–backed relapse pattern labeler invoked from the service layer on relapse.

All **Kotlin/Python identifiers and comments are in English**. **User-visible Android copy** is localized via `res/values/strings.xml` (default) and `res/values-uk/strings.xml` (Ukrainian).

---

## Repository layout

```
health-tracker/
  android/                 # Gradle project (open this folder in Android Studio)
    app/
      src/main/java/...    # Compose UI, Room, Retrofit, ViewModels
      src/main/res/...     # themes + strings (en + uk)
  backend/
    app/
      api/routes/          # FastAPI routers (/api/v1/...)
      core/                # settings + auth stub
      db/                  # async engine + session
      models/              # SQLAlchemy ORM entities
      schemas/             # Pydantic DTOs (API contracts)
      services/            # business logic + ML orchestration
      ml/                  # scikit-learn classifier
    requirements.txt
  README.md
```

---

## Server setup (Python + PostgreSQL)

### 1. Prerequisites

- Python **3.11+**
- A running **PostgreSQL** instance (local or managed cloud).

### 2. Create database and user

```sql
CREATE DATABASE health_tracker;
CREATE USER health_user WITH PASSWORD 'health_pass';
GRANT ALL PRIVILEGES ON DATABASE health_tracker TO health_user;
```

### 3. Configure environment

```powershell
cd health-tracker\backend
copy .env.example .env
```

Edit `.env` and set `DATABASE_URL`, for example:

`postgresql+asyncpg://health_user:health_pass@localhost:5432/health_tracker`

### 4. Virtual environment and dependencies

**Windows (PowerShell)**

```powershell
cd health-tracker\backend
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
```

### 5. Run the API

```powershell
cd health-tracker\backend
python -m uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

Open `http://127.0.0.1:8000/docs` for interactive OpenAPI.

> **Note:** `app/main.py` calls `Base.metadata.create_all` on startup for the demo. Use **Alembic** migrations for production.

### 6. Auth header contract (demo vs Firebase)

`app/core/security.py` contains a **development-oriented** bearer stub (`Authorization: Bearer <token>` → pseudo user id). Replace this with real **Firebase ID token verification** (JWKS or `firebase-admin`) before production.

---

## Android setup (Android Studio)

### 1. Open the Gradle project

Use **File → Open…** and select the `health-tracker/android` directory.

### 2. Emulator networking

The template `BuildConfig.API_BASE_URL` defaults to `http://10.0.2.2:8000/` (host loopback from the Android emulator). For a physical device, use your machine LAN IP, e.g. `http://192.168.1.10:8000/`.

You can override the base URL in `android/local.properties`:

```properties
healthtracker.api.baseUrl=http://10.0.2.2:8000/
```

### 3. Firebase (optional for first compile)

The app module **applies** the Google Services plugin **only if** `android/app/google-services.json` exists. Add the file from the Firebase console when you are ready to enable **Google Sign-In**.

Until then, **debug builds** send a long dev bearer token (`AuthRepository`) so you can still exercise the REST API against the FastAPI stub.

### 4. Run configuration

Select the `app` configuration and run on an API **26+** device or emulator.

---

## How the Android client maps to REST routes

| Feature | Retrofit interface | HTTP |
| --- | --- | --- |
| Sync / create server profile | `UsersApi.sync` | `POST /api/v1/users/sync` |
| Current profile | `UsersApi.me` | `GET /api/v1/users/me` |
| Successful day | `HabitsApi.checkIn` | `POST /api/v1/habits/check-in` |
| Relapse + ML insight | `HabitsApi.relapse` | `POST /api/v1/habits/relapse` |
| History cache refresh | `HabitsApi.logs` | `GET /api/v1/habits/logs` |
| Community feed | `CommunityApi.posts` | `GET /api/v1/community/posts` |
| Upvote / support | `CommunityApi.upvote` | `POST /api/v1/community/posts/{id}/upvote` |
| Analysis bundle | `AnalyticsApi.summary` | `GET /api/v1/analytics/summary` |

Example **curl** (relapse triggers ML classification server-side):

```bash
curl -X POST "http://127.0.0.1:8000/api/v1/habits/relapse" ^
  -H "Authorization: Bearer devtoken1234567890" ^
  -H "Content-Type: application/json" ^
  -d "{\"time_of_day_bucket\":\"evening\",\"stress_level\":8,\"reason_text\":\"deadline stress panic\"}"
```

---

## ML integration (server)

`app/services/habit_service.py` calls `app.ml.classify.classify_relapse(...)` when a relapse is recorded. The classifier trains a **small in-memory model** on synthetic rows at first use, or loads a persisted `joblib` pipeline if `ML_MODEL_PATH` points to a file.

Train and export your own artifact in a notebook or script, then set:

`ML_MODEL_PATH=C:\path\to\relapse_model.joblib`

---

## Design alignment

Compose screens follow the **dark, mint-accent** layout described in your brief: large streak counter, primary/secondary actions, community cards, analysis strip + sparkline, profile controls. Extend typography and spacing tokens in `android/app/src/main/java/com/healthtracker/app/ui/theme/`.

---

## License

Template code for educational / bootstrapping purposes; apply your own license before shipping.
