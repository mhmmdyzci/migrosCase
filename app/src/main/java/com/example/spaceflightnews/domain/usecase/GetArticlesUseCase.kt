package com.example.spaceflightnews.domain.usecase

import android.content.Context
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.network.repository.NewsRepository

class GetArticlesUseCase(private val repository: NewsRepository) {
    
    suspend operator fun invoke(limit: Int, offset: Int, context: Context): Result<List<Article>> {
        return try {
            val articles = repository.getArticles(limit, offset, context)
            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
