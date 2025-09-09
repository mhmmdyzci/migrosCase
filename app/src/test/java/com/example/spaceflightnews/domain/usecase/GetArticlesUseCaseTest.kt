package com.example.spaceflightnews.domain.usecase

import android.content.Context
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.network.repository.NewsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetArticlesUseCaseTest {

    private lateinit var repository: NewsRepository
    private lateinit var getArticlesUseCase: GetArticlesUseCase
    private lateinit var context: Context

    @Before
    fun setUp() {
        repository = mockk()
        context = mockk()
        getArticlesUseCase = GetArticlesUseCase(repository)
    }

    @Test
    fun `invoke should return success when repository returns articles`() = runTest {
        // Given
        val expectedArticles = listOf(
            Article(
                id = 1,
                title = "Test Article 1",
                summary = "Test Summary 1",
                imageUrl = "https://example.com/image1.jpg",
                publishedAt = "2023-01-01T00:00:00Z",
                url = "https://example.com/article1",
                isFavorite = false
            ),
            Article(
                id = 2,
                title = "Test Article 2",
                summary = "Test Summary 2",
                imageUrl = "https://example.com/image2.jpg",
                publishedAt = "2023-01-02T00:00:00Z",
                url = "https://example.com/article2",
                isFavorite = false
            )
        )
        
        coEvery { 
            repository.getArticles(any(), any(), any()) 
        } returns Result.success(expectedArticles)

        // When
        val result = getArticlesUseCase(limit = 10, offset = 0, context = context)

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedArticles, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository throws exception`() = runTest {
        // Given
        val expectedException = Exception("Network error")
        coEvery { 
            repository.getArticles(any(), any(), any()) 
        } returns Result.failure(expectedException)

        // When
        val result = getArticlesUseCase(limit = 10, offset = 0, context = context)

        // Then
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }

    @Test
    fun `invoke should pass correct parameters to repository`() = runTest {
        // Given
        val limit = 20
        val offset = 10
        coEvery { 
            repository.getArticles(limit, offset, context) 
        } returns Result.success(emptyList())

        // When
        getArticlesUseCase(limit = limit, offset = offset, context = context)

        // Then
        coEvery { repository.getArticles(limit, offset, context) }
    }
}
