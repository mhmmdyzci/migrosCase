package com.example.spaceflightnews.network.repository

import com.example.spaceflightnews.domain.model.Article

interface NewsRepository {
    suspend fun getArticles(limit: Int, offset: Int): List<Article>
    suspend fun getArticleById(id: Int): Article

}