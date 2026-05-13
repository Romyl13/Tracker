package com.healthtracker.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val authorDisplayName: String?,
    val title: String,
    val body: String,
    val upvotes: Int,
    val createdAtEpochMs: Long
)
