package com.healthtracker.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "habit_logs")
data class HabitLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val eventType: String,
    val occurredAtEpochMs: Long,
    val xpAwarded: Int,
    val timeOfDayBucket: String?,
    val stressLevel: Int?,
    val reasonText: String?,
    val mlLabel: String?,
    val pendingSync: Boolean = false
)
