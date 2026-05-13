package com.healthtracker.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.healthtracker.app.data.local.dao.HabitLogDao
import com.healthtracker.app.data.local.dao.PostDao
import com.healthtracker.app.data.local.dao.UserProfileDao
import com.healthtracker.app.data.local.entity.HabitLogEntity
import com.healthtracker.app.data.local.entity.PostEntity
import com.healthtracker.app.data.local.entity.UserProfileEntity

@Database(
    entities = [HabitLogEntity::class, PostEntity::class, UserProfileEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitLogDao(): HabitLogDao
    abstract fun postDao(): PostDao
    abstract fun profileDao(): UserProfileDao
}
