package com.healthtracker.app.data.repo

import com.healthtracker.app.data.local.AppDatabase
import com.healthtracker.app.data.local.LocalRelapseClassifier
import com.healthtracker.app.data.local.model.LocalAnalyticsSummary
import com.healthtracker.app.data.local.model.LocalHabitLogRef
import com.healthtracker.app.data.local.model.LocalInsight
import com.healthtracker.app.data.local.entity.UserProfileEntity

class AnalyticsRepository(
    private val db: AppDatabase
) {
    suspend fun loadSummary(): LocalAnalyticsSummary {
        val profile = db.profileDao().getOne() ?: defaultProfile()
        val logs = db.habitLogDao().snapshot()
        val checkIns = logs.count { it.eventType == "check_in" }
        val relapses = logs.count { it.eventType == "relapse" }
        val denom = checkIns + relapses
        val ratio = if (denom > 0) checkIns.toDouble() / denom else 1.0

        val relEntities = logs.filter { it.eventType == "relapse" }
        val relapseHistory = relEntities.map { LocalHabitLogRef(it.occurredAtEpochMs, it.eventType) }

        val insights = relEntities.mapNotNull { log ->
            val bucket = log.timeOfDayBucket ?: return@mapNotNull null
            val stress = log.stressLevel ?: return@mapNotNull null
            val reason = log.reasonText ?: return@mapNotNull null
            val (_, human) = LocalRelapseClassifier.classify(bucket, stress, reason)
            LocalInsight(title = "Інсайт (на пристрої)", detail = human)
        }.takeLast(10).reversed()

        val moneySaved = profile.dailyCostUah * profile.streakDays.coerceAtLeast(0)

        return LocalAnalyticsSummary(
            streakDays = profile.streakDays,
            totalXp = profile.totalXp,
            successfulDaysRatio = ratio,
            moneySavedUah = moneySaved,
            relapseHistory = relapseHistory,
            insights = insights
        )
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
