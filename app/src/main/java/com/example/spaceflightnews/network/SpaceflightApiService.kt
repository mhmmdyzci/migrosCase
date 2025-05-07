package com.example.spaceflightnews.network

import com.example.spaceflightnews.data.remote.dto.ArticleDto
import com.example.spaceflightnews.data.remote.dto.ArticleResponseDto
import retrofit2.http.*


interface SpaceflightApiService {

    @GET("articles")
    suspend fun getArticles(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): ArticleResponseDto

    @GET("articles/{id}")
    suspend fun getArticleById(
        @Path("id") id: Int
    ): ArticleDto
}