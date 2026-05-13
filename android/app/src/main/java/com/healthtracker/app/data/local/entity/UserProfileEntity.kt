package com.healthtracker.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val displayName: String?,
    val email: String?,
    val streakDays: Int,
    val totalXp: Int,
    val dailyCostUah: Double,
    val notificationsEnabled: Boolean
)
