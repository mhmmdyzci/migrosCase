package com.example.spaceflightnews.data.remote.dto


data class ArticleResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ArticleDto>
)