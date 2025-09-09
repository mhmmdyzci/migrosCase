package com.example.spaceflightnews.presentation.fragment.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceflightnews.core.SpaceflightApp
import com.example.spaceflightnews.databinding.FragmentHomeBinding
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.domain.usecase.*
import com.example.spaceflightnews.network.SpaceflightApiService
import com.example.spaceflightnews.network.repository.NewsRepositoryImpl
import com.example.spaceflightnews.presentation.fragment.home.adapter.ArticleAdapter
import com.example.spaceflightnews.util.BaseFragment
import com.example.spaceflightnews.util.NetworkUtil
import com.example.spaceflightnews.util.PreferenceHelper
import com.example.spaceflightnews.viewModel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import com.example.spaceflightnews.R
import java.util.Locale
import androidx.core.view.isVisible

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var articleAdapter: ArticleAdapter
    private var articleList: List<Article> = emptyList()
    private var isSwipeRefreshing = false
    private var lastToastTime = 0L
    private val toastCooldownMillis = 2000L



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupSwipeToRefresh()
        setupSearch()
        setupSearchUI()
        setupStatusBar()
        setupRetryButton()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        decideInitialLoad()
    }

    private fun initViewModel() {
        val api = SpaceflightApp.retrofit.create(SpaceflightApiService::class.java)
        val dao = SpaceflightApp.articleDao
        val repository = NewsRepositoryImpl(api, dao)

        val getArticlesUseCase = GetArticlesUseCase(repository)
        val getCachedArticlesUseCase = GetCachedArticlesUseCase(repository)

        viewModel = HomeViewModel(
            getArticlesUseCase,
            getCachedArticlesUseCase
        )
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(requireContext()) { article ->
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(article)
            findNavController().navigate(action)
        }
        binding.articleRecyclerView.adapter = articleAdapter

        binding.articleRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                val isLastItemVisible = lastVisibleItemPosition == totalItemCount - 1

                if (isLastItemVisible && dy > 0) {
                    if (!viewModel.isLoading() && !viewModel.isLastPage() && !viewModel.isCurrentlySearching()) {
                        if (NetworkUtil.isNetworkAvailable(requireContext())) {
                            viewModel.fetchArticles(requireContext())
                        } else {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastToastTime > toastCooldownMillis) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.no_internet_connection),
                                    Toast.LENGTH_SHORT
                                ).show()
                                lastToastTime = currentTime
                            }
                        }
                    }
                }
            }
        })
    }


    private fun setupObservers() {
        viewModel.articles.observe(viewLifecycleOwner) {
            articleList = it
            articleAdapter.submitList(it)
            updateStatusBar()
        }



        viewModel.loading.observe(viewLifecycleOwner) {
            if (isSwipeRefreshing) {
                binding.swipeRefreshLayout.isRefreshing = false
                isSwipeRefreshing = false
            } else{
                isShowMainLoading(it)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                binding.errorMessage.text = it
            }
        }
        
        viewModel.showRetry.observe(viewLifecycleOwner) { showRetry ->
            animateViewVisibility(binding.errorLayout, showRetry)
            animateViewVisibility(binding.articleRecyclerView, !showRetry)
        }


    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.apply {
            setColorSchemeResources(
                R.color.blue,
                R.color.dark_blue
            )
            setOnRefreshListener {
                when {
                    !NetworkUtil.isNetworkAvailable(requireContext()) -> {
                        isRefreshing = false
                        Toast.makeText(requireContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show()
                    }
                    viewModel.isLoading() -> {
                        isRefreshing = false
                        Toast.makeText(requireContext(), "Already loading...", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        isSwipeRefreshing = true
                        viewModel.refreshFromApi(requireContext())
                    }
                }
            }
        }
    }

    private fun setupSearch() {
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                startIndex: Int,
                removedCharCount: Int,
                addedCharCount: Int
            ) {
            }

            override fun onTextChanged(
                text: CharSequence?,
                startIndex: Int,
                beforeCharCount: Int,
                newCharCount: Int
            ) {
            }

            override fun afterTextChanged(text: Editable?) {
                val query = text.toString().trim()
                if (query.isEmpty()) {
                    articleAdapter.submitList(articleList)
                    viewModel.isSearching = false
                } else {
                    viewModel.isSearching = true
                    val filteredList = articleList.filter { article ->
                        article.title.contains(query, ignoreCase = true) ||
                                article.summary.contains(query, ignoreCase = true)
                    }
                    articleAdapter.submitList(filteredList)
                }
            }
        })
    }

    private fun setupSearchUI() {
        binding.search.setOnClickListener {
            val bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_bounce)
            binding.search.startAnimation(bounceAnimation)
            showSearchBar()
        }

        binding.backButton.setOnClickListener {
            val bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_bounce)
            binding.backButton.startAnimation(bounceAnimation)
            hideSearchBar()
        }

        binding.clearSearch.setOnClickListener {
            val bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_bounce)
            binding.clearSearch.startAnimation(bounceAnimation)
            binding.editTextSearch.setText("")
        }
    }

    private fun decideInitialLoad() {
        if (NetworkUtil.isNetworkAvailable(requireContext())) {
            viewModel.refreshFromApi(requireContext())
        } else {
            viewModel.loadCachedArticles()
        }
    }
    
    private fun setupStatusBar() {
        updateStatusBar()
    }
    
    private fun setupRetryButton() {
        binding.retryButton.setOnClickListener {
            viewModel.retryFetchArticles(requireContext())
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateStatusBar() {
        val lastUpdateTime = PreferenceHelper.getLastUpdateTime(requireContext())
        val isOnline = NetworkUtil.isNetworkAvailable(requireContext())
        
        if (lastUpdateTime == -1L || articleList.isEmpty()) {
            animateViewVisibility(binding.statusBar, false)
            return
        }
        
        animateViewVisibility(binding.statusBar, true)
        
        val timeAgo = getTimeAgoString(lastUpdateTime)
        
        if (isOnline) {
            binding.statusIndicator.setBackgroundResource(R.drawable.status_indicator_online)
            binding.statusText.text = getString(R.string.updated_time_ago, timeAgo)
        } else {
            binding.statusIndicator.setBackgroundResource(R.drawable.status_indicator_offline)
            binding.statusText.text = getString(R.string.showing_cached_data) + " • " + 
                getString(R.string.last_updated, timeAgo)
        }
    }
    
    private fun getTimeAgoString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60000 -> getString(R.string.just_now)
            diff < 3600000 -> {
                val minutes = (diff / 60000).toInt()
                getString(R.string.minutes_ago, minutes)
            }
            diff < 86400000 -> {
                val hours = (diff / 3600000).toInt()
                getString(R.string.hours_ago, hours)
            }
            else -> {
                val days = (diff / 86400000).toInt()
                getString(R.string.days_ago, days)
            }
        }
    }

    fun getFormattedLastUpdate(): String {
        val lastUpdate = PreferenceHelper.getLastUpdateTime(requireContext())
        return if (lastUpdate != -1L) {
            val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
            "Last Update: ${sdf.format(Date(lastUpdate))}"
        } else {
            ""
        }
    }

    fun refreshList() {
        articleAdapter.submitList(articleList)
    }

    private fun filterList(searchQuery: String, originalList: List<Article>) {
        val filteredList = originalList.filter { article ->
            article.title.contains(searchQuery, ignoreCase = true) == true
        }
        articleAdapter.submitList(filteredList)
    }

    private fun showSearchBar() {
        val slideOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_top)
        binding.header.startAnimation(slideOutAnimation)
        animateViewVisibility(binding.header, false)
        
        animateViewVisibility(binding.searchLayout, true)
        val slideInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_top)
        binding.searchLayout.startAnimation(slideInAnimation)
        
        binding.editTextSearch.requestFocus()
        showKeyboard(binding.editTextSearch)
    }

    private fun hideSearchBar() {
        val slideOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_top)
        binding.searchLayout.startAnimation(slideOutAnimation)
        animateViewVisibility(binding.searchLayout, false)
        
        animateViewVisibility(binding.header, true)
        val slideInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_top)
        binding.header.startAnimation(slideInAnimation)
        
        binding.editTextSearch.setText("")
        hideKeyboard(binding.editTextSearch)
        viewModel.isSearching = false
        refreshList()
    }

    private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun animateViewVisibility(view: View, show: Boolean) {
        if (show && view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
            val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
            view.startAnimation(fadeInAnimation)
        } else if (!show && view.isVisible) {
            val fadeOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
            view.startAnimation(fadeOutAnimation)
            view.visibility = View.GONE
        }
    }
}

