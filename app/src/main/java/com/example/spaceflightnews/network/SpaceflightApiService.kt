package com.example.spaceflightnews.network

import com.example.spaceflightnews.data.remote.dto.ArticleDto
import com.example.spaceflightnews.data.remote.dto.ArticleResponseDto
import com.example.spaceflightnews.util.Constants
import retrofit2.http.*


interface SpaceflightApiService {

    @GET(Constants.EndPoints.ARTICLES)
    suspend fun getArticles(
        @Query(Constants.Queries.LIMIT) limit: Int,
        @Query(Constants.Queries.OFFSET) offset: Int
    ): ArticleResponseDto

    @GET(Constants.EndPoints.ARTICLE)
    suspend fun getArticleById(
        @Path(Constants.Paths.ARTICLE_ID) id: Int
    ): ArticleDto
}