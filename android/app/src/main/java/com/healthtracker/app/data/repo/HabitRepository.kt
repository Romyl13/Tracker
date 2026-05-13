package com.healthtracker.app.data.repo

import androidx.room.withTransaction
import com.healthtracker.app.data.local.AppDatabase
import com.healthtracker.app.data.local.LocalRelapseClassifier
import com.healthtracker.app.data.local.entity.HabitLogEntity
import com.healthtracker.app.data.local.entity.UserProfileEntity
import java.util.UUID

private const val CHECK_IN_XP = 25
private const val EVENT_CHECK_IN = "check_in"
private const val EVENT_RELAPSE = "relapse"

class HabitRepository(
    private val db: AppDatabase
) {
    private val logs = db.habitLogDao()
    private val profileDao = db.profileDao()

    fun observeLogs() = logs.observeLogs()

    suspend fun refreshFromServer() {
        // Offline-only: no remote sync.
    }

    suspend fun checkIn(note: String?): Result<Unit> =
        runCatching {
            db.withTransaction {
                val p = profileDao.getOne() ?: defaultProfile()
                profileDao.upsert(
                    p.copy(
                        streakDays = p.streakDays + 1,
                        totalXp = p.totalXp + CHECK_IN_XP
                    )
                )
                logs.insert(
                    HabitLogEntity(
                        id = UUID.randomUUID().toString(),
                        eventType = EVENT_CHECK_IN,
                        occurredAtEpochMs = System.currentTimeMillis(),
                        xpAwarded = CHECK_IN_XP,
                        timeOfDayBucket = null,
                        stressLevel = null,
                        reasonText = note,
                        mlLabel = null,
                        pendingSync = false
                    )
                )
            }
            Unit
        }

    suspend fun relapse(bucket: String, stress: Int, reason: String): Result<Unit> =
        runCatching {
            val (labelKey, _) = LocalRelapseClassifier.classify(bucket, stress, reason)
            db.withTransaction {
                val p = profileDao.getOne() ?: defaultProfile()
                profileDao.upsert(p.copy(streakDays = 0))
                logs.insert(
                    HabitLogEntity(
                        id = UUID.randomUUID().toString(),
                        eventType = EVENT_RELAPSE,
                        occurredAtEpochMs = System.currentTimeMillis(),
                        xpAwarded = 0,
                        timeOfDayBucket = bucket,
                        stressLevel = stress,
                        reasonText = reason,
                        mlLabel = labelKey,
                        pendingSync = false
                    )
                )
            }
            Unit
        }

    private fun defaultProfile() = UserProfileEntity(
        displayName = null,
        email = null,
        streakDays = 0,
        totalXp = 0,
        dailyCostUah = 120.0,
        notificationsEnabled = true
    )
}
