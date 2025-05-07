package com.example.spaceflightnews.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.spaceflightnews.core.SpaceflightApp
import com.example.spaceflightnews.databinding.FragmentHomeBinding
import com.example.spaceflightnews.network.SpaceflightApiService
import com.example.spaceflightnews.network.repository.NewsRepositoryImpl
import com.example.spaceflightnews.util.BaseFragment
import com.example.spaceflightnews.viewModel.NewsViewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var viewModel: NewsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val api = SpaceflightApp.retrofit.create(SpaceflightApiService::class.java)
        val dao = SpaceflightApp.articleDao
        val repository = NewsRepositoryImpl(api, dao)

        viewModel = NewsViewModel(repository)

        viewModel.articles.observe(viewLifecycleOwner) {
            Log.d("MainActivity", "Gelen makale sayısı: ${it.size}")
        }

        viewModel.fetchArticles(limit = 10, offset = 0)
    }
}