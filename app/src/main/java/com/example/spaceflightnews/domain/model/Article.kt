package com.example.spaceflightnews.domain.model

data class Article(
    val id: Int,
    val title: String,
    val summary: String,
    val imageUrl: String?,
    val publishedAt: String,
    val url: String,
    val isFavorite: Boolean = false,
    val cachedAt : Long = System.currentTimeMillis()
)