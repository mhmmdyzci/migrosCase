package com.example.spaceflightnews.network.repository

import com.example.spaceflightnews.data.local.dao.ArticleDao
import com.example.spaceflightnews.data.local.entity.toDomain
import com.example.spaceflightnews.data.remote.dto.toEntity
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.network.SpaceflightApiService

class NewsRepositoryImpl(
    private val api: SpaceflightApiService,
    private val dao: ArticleDao
) : NewsRepository {

    override suspend fun getArticles(limit: Int, offset: Int): List<Article> {
        return try {
            val response = api.getArticles(limit, offset)
            val entities = response.results.map { it.toEntity() }

            dao.clearArticles()
            dao.insertArticles(entities)

            entities.map { it.toDomain() }
        } catch (e: Exception) {
            dao.getAllArticles().map { it.toDomain() }
        }
    }

    override suspend fun getArticleById(id: Int): Article {
        return try {
            api.getArticleById(id).toEntity().toDomain()
        } catch (e: Exception) {
            dao.getArticleById(id)?.toDomain()
                ?: throw Exception("Article not found")
        }
    }
}