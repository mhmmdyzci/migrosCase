package com.example.spaceflightnews.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.spaceflightnews.domain.model.Article

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val summary: String,
    val imageUrl: String?,
    val publishedAt: String,
    val url: String,
    val isFavorite: Boolean = false,
    val cachedAt: Long
)

fun ArticleEntity.toDomain(): Article {
    return Article(
        id = id,
        title = title,
        summary = summary,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        url = url,
        isFavorite = isFavorite,
        cachedAt = cachedAt
    )
}