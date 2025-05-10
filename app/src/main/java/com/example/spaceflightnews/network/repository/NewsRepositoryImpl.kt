package com.example.spaceflightnews.network.repository

import android.content.Context
import com.example.spaceflightnews.core.SpaceflightApp
import com.example.spaceflightnews.data.local.dao.ArticleDao
import com.example.spaceflightnews.data.local.entity.toDomain
import com.example.spaceflightnews.data.remote.dto.toEntity
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.network.SpaceflightApiService
import com.example.spaceflightnews.util.PreferenceHelper

class NewsRepositoryImpl(
    private val api: SpaceflightApiService,
    private val dao: ArticleDao
) : NewsRepository {

    override suspend fun getArticles(limit: Int, offset: Int, context: Context): List<Article> {
        val response = api.getArticles(limit, offset)
        val entities = response.results.map { it.toEntity() }

        if (offset == 0) {
            dao.clearArticles()
        }
        PreferenceHelper.saveLastUpdateTime(context, System.currentTimeMillis())

        dao.insertArticles(entities)
        return entities.map { it.toDomain() }

    }

    override suspend fun getArticleById(id: Int): Article {
        return try {
            api.getArticleById(id).toEntity().toDomain()
        } catch (e: Exception) {
            dao.getArticleById(id)?.toDomain()
                ?: throw Exception("Article not found")
        }
    }

    override suspend fun getCachedArticles(): List<Article> {
        return dao.getAllArticles().map { it.toDomain() }
    }
}