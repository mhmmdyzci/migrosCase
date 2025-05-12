package com.example.spaceflightnews.data.remote.dto

import com.example.spaceflightnews.data.local.entity.ArticleEntity
import com.google.gson.annotations.SerializedName

data class ArticleDto(
    val id: Int,
    val title: String,
    val summary: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("published_at") val publishedAt: String,
    val url: String,
    val cachedAt : Long = System.currentTimeMillis()
)

fun ArticleDto.toEntity(): ArticleEntity {
    return ArticleEntity(
        id = id,
        title = title,
        summary = summary,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        url = url,
        cachedAt = cachedAt

    )
}