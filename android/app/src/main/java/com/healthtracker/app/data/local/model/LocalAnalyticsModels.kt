package com.healthtracker.app.data.local.model

/**
 * Offline analytics bundle built from Room (replaces the former REST DTO).
 */
data class LocalAnalyticsSummary(
    val streakDays: Int,
    val totalXp: Int,
    val successfulDaysRatio: Double,
    val moneySavedUah: Double,
    val relapseHistory: List<LocalHabitLogRef>,
    val insights: List<LocalInsight>
)

data class LocalHabitLogRef(
    val occurredAtEpochMs: Long,
    val eventType: String
)

data class LocalInsight(
    val title: String,
    val detail: String
)
