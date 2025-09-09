package com.example.spaceflightnews.domain.usecase

import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.network.repository.NewsRepository

class GetArticleByIdUseCase(private val repository: NewsRepository) {
    
    suspend operator fun invoke(id: Int): Result<Article> {
        return try {
            val article = repository.getArticleById(id)
            Result.success(article)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
