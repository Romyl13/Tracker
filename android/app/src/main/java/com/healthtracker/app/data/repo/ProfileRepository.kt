package com.healthtracker.app.data.repo

import com.healthtracker.app.data.local.dao.UserProfileDao
import com.healthtracker.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.first

class ProfileRepository(
    private val dao: UserProfileDao
) {
    fun observeProfile() = dao.observeProfile()

    suspend fun seedIfEmpty() {
        val existing = dao.observeProfile().first()
        if (existing == null) {
            dao.upsert(
                UserProfileEntity(
                    displayName = null,
                    email = null,
                    streakDays = 0,
                    totalXp = 0,
                    dailyCostUah = 120.0,
                    notificationsEnabled = true
                )
            )
        }
    }

    suspend fun clearLocalIdentity() {
        val cur = dao.getOne() ?: return
        dao.upsert(cur.copy(displayName = null, email = null))
    }

    suspend fun setNotifications(enabled: Boolean) {
        val cur = dao.getOne() ?: UserProfileEntity(
            displayName = null,
            email = null,
            streakDays = 0,
            totalXp = 0,
            dailyCostUah = 120.0,
            notificationsEnabled = enabled
        )
        dao.upsert(cur.copy(notificationsEnabled = enabled))
    }
}
