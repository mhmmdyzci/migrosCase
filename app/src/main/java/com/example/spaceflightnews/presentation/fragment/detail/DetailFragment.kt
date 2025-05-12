package com.example.spaceflightnews.presentation.fragment.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.spaceflightnews.R
import com.example.spaceflightnews.core.SpaceflightApp
import com.example.spaceflightnews.databinding.FragmentDetailBinding
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.network.SpaceflightApiService
import com.example.spaceflightnews.network.repository.NewsRepositoryImpl
import com.example.spaceflightnews.util.BaseFragment
import com.example.spaceflightnews.util.DateUtil.formatDate
import com.example.spaceflightnews.viewModel.NewsViewModel

class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {
    private lateinit var article: Article
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var viewModel: NewsViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        article = args.article
        initViewModel()
        viewModel.isArticleFavorited(args.article.id)
        setupUI()

    }

    private fun initViewModel() {
        val api = SpaceflightApp.Companion.retrofit.create(SpaceflightApiService::class.java)
        val dao = SpaceflightApp.Companion.articleDao
        viewModel = NewsViewModel(NewsRepositoryImpl(api, dao))
    }

    private fun setupUI() {
        binding.apply {
            detailTitle.text = article.title
            detailSummary.text = article.summary
            detailDate.text = formatDate(article.publishedAt)

            if (!article.imageUrl.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(article.imageUrl)
                    .into(detailImage)
            } else {
                detailImage.setImageResource(R.drawable.ic_launcher_background)
            }



            btnFavorite.setOnClickListener {
                val currentStatus = viewModel.isFavorite.value ?: false
                val newStatus = !currentStatus
                viewModel.updateFavorite(article.id, newStatus)
                viewModel._isFavorite.value = newStatus
                if (newStatus) {
                    Toast.makeText(requireContext(),getString(R.string.added_to_favorites), Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(requireContext(), getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show()

                }

            }

            btnShare.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, article.title)
                    putExtra(Intent.EXTRA_TEXT, article.url)
                }
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }

            viewModel.isFavorite.observe(viewLifecycleOwner) {
                if (it) {
                    binding.btnFavorite.setImageResource(R.drawable.icons_added_bookmark)
                } else {
                    binding.btnFavorite.setImageResource(R.drawable.icon_bookmark)
                }
            }

        }

    }
}