package com.example.spaceflightnews.domain.usecase

import com.example.spaceflightnews.network.repository.NewsRepository

class UpdateFavoriteUseCase(private val repository: NewsRepository) {
    
    suspend operator fun invoke(id: Int, isFavorite: Boolean): Result<Unit> {
        return try {
            repository.updateFavoriteStatus(id, isFavorite)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
