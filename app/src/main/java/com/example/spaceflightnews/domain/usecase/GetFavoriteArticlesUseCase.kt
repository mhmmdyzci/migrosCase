package com.example.spaceflightnews.domain.usecase

import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.network.repository.NewsRepository

class GetFavoriteArticlesUseCase(private val repository: NewsRepository) {
    
    suspend operator fun invoke(): Result<List<Article>> {
        return try {
            val favoriteArticles = repository.getFavoriteArticles()
            Result.success(favoriteArticles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
