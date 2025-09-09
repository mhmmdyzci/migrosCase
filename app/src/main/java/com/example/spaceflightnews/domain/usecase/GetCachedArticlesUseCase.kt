package com.example.spaceflightnews.domain.usecase

import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.network.repository.NewsRepository

class GetCachedArticlesUseCase(private val repository: NewsRepository) {
    
    suspend operator fun invoke(): Result<List<Article>> {
        return try {
            val cachedArticles = repository.getCachedArticles()
            Result.success(cachedArticles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
