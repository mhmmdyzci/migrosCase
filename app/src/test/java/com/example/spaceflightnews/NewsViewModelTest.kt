package com.example.spaceflightnews

import com.example.spaceflightnews.viewModel.NewsViewModel
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.network.repository.NewsRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: NewsRepository
    private lateinit var viewModel: NewsViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = mockk(relaxed = true)
        viewModel = NewsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchArticles should update articles`() = runTest {
        val mockArticles = listOf(
            Article(1, "Title", "Summary", null, "2024-01-01", "url", false, 0)
        )
        coEvery { repository.getArticles(any(), any(), any()) } returns mockArticles

        viewModel.fetchArticles(mockk(relaxed = true))
        advanceUntilIdle()

        assertEquals(mockArticles, viewModel.articles.value)
    }

    @Test
    fun `loadCachedArticles should update articles`() = runTest {
        val cached = listOf(
            Article(2, "Cached Title", "Cached Summary", null, "2024-01-01", "url", false, 0)
        )
        coEvery { repository.getCachedArticles() } returns cached

        viewModel.loadCachedArticles()
        advanceUntilIdle()

        assertEquals(cached, viewModel.articles.value)
    }

    @Test
    fun `updateFavorite should call repository`() = runTest {
        val testId = 1
        val testStatus = true
        coEvery { repository.updateFavoriteStatus(testId, testStatus) } just Runs

        viewModel.updateFavorite(testId, testStatus)
        advanceUntilIdle()
        coVerify(exactly = 1) { repository.updateFavoriteStatus(testId, testStatus) }
    }


    @Test
    fun `getFavoriteArticles should update favoriteArticles`() = runTest {
        val favs = listOf(
            Article(5, "Fav", "Summary", null, "2024-01-01", "url", true, 0)
        )
        coEvery { repository.getFavoriteArticles() } returns favs

        viewModel.getFavoriteArticles()
        advanceUntilIdle()

        assertEquals(favs, viewModel.favoriteArticles.value)
    }

    @Test
    fun `isArticleFavorited should update isFavorite`() = runTest {
        coEvery { repository.isArticleFavorited(1) } returns true

        viewModel.isArticleFavorited(1)
        advanceUntilIdle()

        assertEquals(true, viewModel.isFavorite.value)
    }

    @Test
    fun `fetchArticles should post error on exception`() = runTest {
        coEvery { repository.getArticles(any(), any(), any()) } throws Exception("Network error")

        viewModel.fetchArticles(mockk(relaxed = true))
        advanceUntilIdle()

        assertEquals("Network error", viewModel.error.value)
    }

    @Test
    fun `loadCachedArticles should post error on failure`() = runTest {
        coEvery { repository.getCachedArticles() } throws Exception("Cache failed")

        viewModel.loadCachedArticles()
        advanceUntilIdle()

        assertEquals("Failed to load cached articles.", viewModel.error.value)
    }

}
