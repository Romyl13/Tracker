package com.healthtracker.app.data.repo

import com.healthtracker.app.data.local.dao.PostDao
import com.healthtracker.app.data.local.entity.PostEntity
import java.util.UUID

class CommunityRepository(
    private val dao: PostDao
) {
    fun observePosts() = dao.observePosts()

    suspend fun ensureSeeded() {
        if (dao.count() > 0) return
        dao.upsertAll(defaultPosts())
    }

    suspend fun refresh() {
        ensureSeeded()
    }

    suspend fun upvote(id: String): Boolean {
        val existing = dao.getById(id) ?: return false
        dao.upsertAll(listOf(existing.copy(upvotes = existing.upvotes + 1)))
        return true
    }

    suspend fun createPost(title: String, bodyText: String): Boolean {
        val post = PostEntity(
            id = UUID.randomUUID().toString(),
            authorDisplayName = "Ви",
            title = title,
            body = bodyText,
            upvotes = 0,
            createdAtEpochMs = System.currentTimeMillis()
        )
        dao.upsertAll(listOf(post))
        return true
    }

    private fun defaultPosts(): List<PostEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            PostEntity(
                id = "seed-1",
                authorDisplayName = "Олександр К.",
                title = "Техніка '5 хвилин'",
                body = "Коли є спокуса — погодься з собою почекати лише 5 хвилин. Часто імпульс слабшає сам.",
                upvotes = 24,
                createdAtEpochMs = now - 3_600_000L
            ),
            PostEntity(
                id = "seed-2",
                authorDisplayName = "Марія П.",
                title = "Подзвони другу",
                body = "Подзвони другу, коли відчуваєш тиск. Короткий дзвінок може змінити рішення.",
                upvotes = 41,
                createdAtEpochMs = now - 7_200_000L
            ),
            PostEntity(
                id = "seed-3",
                authorDisplayName = "Андрій В.",
                title = "Вода й прогулянка",
                body = "Заміни ритуал: склянка води + 10 хвилин на свіжому повітрі замість автоматичної дії.",
                upvotes = 18,
                createdAtEpochMs = now - 86_400_000L
            )
        )
    }
}
