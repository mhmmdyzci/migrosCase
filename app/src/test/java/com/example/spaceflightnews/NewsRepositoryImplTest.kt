package com.example.spaceflightnews

import android.content.Context
import com.example.spaceflightnews.viewModel.NewsViewModel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.spaceflightnews.data.local.dao.ArticleDao
import com.example.spaceflightnews.data.local.entity.ArticleEntity
import com.example.spaceflightnews.data.remote.dto.ArticleDto
import com.example.spaceflightnews.data.remote.dto.ArticleResponseDto
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.network.SpaceflightApiService
import com.example.spaceflightnews.network.repository.NewsRepository
import com.example.spaceflightnews.network.repository.NewsRepositoryImpl
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepositoryImplTest {

    private lateinit var api: SpaceflightApiService
    private lateinit var dao: ArticleDao
    private lateinit var repository: NewsRepositoryImpl

    private val dispatcher = StandardTestDispatcher()
    private val context: Context = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        api = mockk()
        dao = mockk(relaxed = true)
        repository = NewsRepositoryImpl(api, dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getArticles should fetch from API and cache to DB`() = runTest {
        val existingEntity = ArticleEntity(1, "Old", "Old", null, "old", "url", true, 0)
        val articleDto = ArticleDto(
            id = 1,
            title = "Example Title",
            summary = "This is a summary",
            imageUrl = null,
            publishedAt = "2024-01-01T00:00:00Z",
            url = "https://example.com"
        )
        val responseDto = ArticleResponseDto(
            count = 1,
            next = null,
            previous = null,
            results = listOf(articleDto)
        )

        coEvery { api.getArticles(any(), any()) } returns responseDto
        coEvery { dao.getAllArticles() } returns listOf(existingEntity)
        coEvery { dao.insertArticles(any()) } just Runs
        coEvery { dao.clearArticles() } just Runs

        val result = repository.getArticles(10, 0, context)

        assertEquals(1, result.size)
        assertEquals("Example Title", result[0].title)
        assertEquals(true, result[0].isFavorite)
    }

    @Test
    fun `getArticleById should return from API if successful`() = runTest {
        val dto = ArticleDto(1, "Title", "Summary", null, "2024-01-01", "url")
        coEvery { api.getArticleById(1) } returns dto

        val result = repository.getArticleById(1)

        assertEquals("Title", result.title)
    }

    @Test
    fun `getArticleById should return from DB if API fails`() = runTest {
        val entity = ArticleEntity(1, "Cached", "Summary", null, "2024-01-01", "url", false, 0)
        coEvery { api.getArticleById(1) } throws Exception()
        coEvery { dao.getArticleById(1) } returns entity

        val result = repository.getArticleById(1)

        assertEquals("Cached", result.title)
    }

    @Test
    fun `getCachedArticles should return from DB`() = runTest {
        val entity = ArticleEntity(1, "Cached", "Summary", null, "2024-01-01", "url", false, 0)
        coEvery { dao.getAllArticles() } returns listOf(entity)

        val result = repository.getCachedArticles()

        assertEquals(1, result.size)
        assertEquals("Cached", result[0].title)
    }

    @Test
    fun `updateFavoriteStatus should update DAO`() = runTest {
        coEvery { dao.updateFavoriteStatus(1, true) } just Runs

        repository.updateFavoriteStatus(1, true)

        coVerify { dao.updateFavoriteStatus(1, true) }
    }

    @Test
    fun `isArticleFavorited should return value from DAO`() = runTest {
        coEvery { dao.isFavorited(1) } returns true

        val result = repository.isArticleFavorited(1)

        assertTrue(result)
    }

    @Test
    fun `getFavoriteArticles should return list from DAO`() = runTest {
        val entity = ArticleEntity(1, "Fav", "Summary", null, "2024-01-01", "url", true, 0)
        coEvery { dao.getFavoriteArticles() } returns listOf(entity)

        val result = repository.getFavoriteArticles()

        assertEquals(1, result.size)
        assertEquals("Fav", result[0].title)
    }
}
