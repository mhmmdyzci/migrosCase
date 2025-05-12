package com.example.spaceflightnews.network.repository

import android.content.Context
import com.example.spaceflightnews.domain.model.Article

interface NewsRepository {
    suspend fun getArticles(limit: Int, offset: Int, context: Context): List<Article>
    suspend fun getArticleById(id: Int): Article
    suspend fun getCachedArticles(): List<Article>
    suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean)
    suspend fun isArticleFavorited(articleId: Int): Boolean
    suspend fun getFavoriteArticles(): List<Article>

}