package com.example.spaceflightnews.presentation.fragment.favorites

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.spaceflightnews.core.SpaceflightApp
import com.example.spaceflightnews.databinding.FragmentFavoritesBinding
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.domain.usecase.*
import com.example.spaceflightnews.network.SpaceflightApiService
import com.example.spaceflightnews.network.repository.NewsRepositoryImpl
import com.example.spaceflightnews.presentation.fragment.home.adapter.ArticleAdapter
import com.example.spaceflightnews.util.BaseFragment
import com.example.spaceflightnews.viewModel.FavoritesViewModel

class FavoritesFragment :
    BaseFragment<FragmentFavoritesBinding>(FragmentFavoritesBinding::inflate) {


    private lateinit var viewModel: FavoritesViewModel
    private lateinit var articleAdapter: ArticleAdapter
    private var articleList: List<Article> = emptyList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        setupRecyclerView()
        setupObservers()
    }

    private fun initViewModel() {
        val api = SpaceflightApp.retrofit.create(SpaceflightApiService::class.java)
        val dao = SpaceflightApp.articleDao
        val repository = NewsRepositoryImpl(api, dao)
        
        // Initialize UseCases for Favorites
        val getFavoriteArticlesUseCase = GetFavoriteArticlesUseCase(repository)
        val updateFavoriteUseCase = UpdateFavoriteUseCase(repository)
        
        viewModel = FavoritesViewModel(
            getFavoriteArticlesUseCase,
            updateFavoriteUseCase
        )
        viewModel.getFavoriteArticles()
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(requireContext()){ article ->
            val action =
                FavoritesFragmentDirections.actionFavoritesFragmentToDetailFragment(article)
            findNavController().navigate(action)
        }
        binding.favoritesRecyclerView.adapter = articleAdapter


    }


    private fun setupObservers() {
        viewModel.favoriteArticles.observe(viewLifecycleOwner) {
            articleList = it
            articleAdapter.submitList(it)
            if (it.isEmpty()) {
                binding.emptyView.visibility = View.VISIBLE
                binding.favoritesRecyclerView.visibility = View.GONE
            } else {
                binding.emptyView.visibility = View.GONE
                binding.favoritesRecyclerView.visibility = View.VISIBLE
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
        }

        viewModel.error.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

}