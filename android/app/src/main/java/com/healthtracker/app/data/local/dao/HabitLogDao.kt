package com.healthtracker.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.healthtracker.app.data.local.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {
    @Query("SELECT * FROM habit_logs ORDER BY occurredAtEpochMs DESC LIMIT 400")
    fun observeLogs(): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<HabitLogEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HabitLogEntity)

    @Query("SELECT * FROM habit_logs ORDER BY occurredAtEpochMs DESC LIMIT 500")
    suspend fun snapshot(): List<HabitLogEntity>
}
