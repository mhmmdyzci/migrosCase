package com.example.spaceflightnews.network.repository

import android.content.Context
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

        val existingArticles = dao.getAllArticles().associateBy { it.id }

        val entities = response.results.map { dto ->
            val oldEntity = existingArticles[dto.id]
            dto.toEntity().copy(isFavorite = oldEntity?.isFavorite ?: false)
        }

        if (offset == 0) {
            dao.clearArticles()
        }

        dao.insertArticles(entities)

        PreferenceHelper.saveLastUpdateTime(context, System.currentTimeMillis())

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

    override suspend fun updateFavoriteStatus(id: Int, isFavorite: Boolean) {
        dao.updateFavoriteStatus(id, isFavorite)
    }

    override suspend fun isArticleFavorited(articleId: Int): Boolean {
        return dao.isFavorited(articleId)
    }

    override suspend fun getFavoriteArticles(): List<Article> {
        return dao.getFavoriteArticles().map { it.toDomain() }
    }

}