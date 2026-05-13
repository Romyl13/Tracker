package com.healthtracker.app.di

import android.content.Context
import androidx.room.Room
import com.healthtracker.app.data.local.AppDatabase
import com.healthtracker.app.data.repo.AnalyticsRepository
import com.healthtracker.app.data.repo.CommunityRepository
import com.healthtracker.app.data.repo.HabitRepository
import com.healthtracker.app.data.repo.ProfileRepository

/**
 * Offline-only composition root: Room is the single source of truth.
 */
class AppGraph(context: Context) {
    private val appContext = context.applicationContext

    val database: AppDatabase = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java,
        "health_tracker.db"
    ).fallbackToDestructiveMigration()
        .build()

    val habitRepository = HabitRepository(database)
    val communityRepository = CommunityRepository(database.postDao())
    val analyticsRepository = AnalyticsRepository(database)
    val profileRepository = ProfileRepository(database.profileDao())
}
