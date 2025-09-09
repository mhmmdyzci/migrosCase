package com.example.spaceflightnews.domain.usecase

import com.example.spaceflightnews.network.repository.NewsRepository

class IsArticleFavoritedUseCase(private val repository: NewsRepository) {
    
    suspend operator fun invoke(articleId: Int): Result<Boolean> {
        return try {
            val isFavorited = repository.isArticleFavorited(articleId)
            Result.success(isFavorited)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
