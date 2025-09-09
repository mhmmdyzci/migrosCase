package com.example.spaceflightnews.presentation.fragment.detail

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.spaceflightnews.R
import com.example.spaceflightnews.core.SpaceflightApp
import com.example.spaceflightnews.databinding.FragmentDetailBinding
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.domain.usecase.*
import com.example.spaceflightnews.network.SpaceflightApiService
import com.example.spaceflightnews.network.repository.NewsRepositoryImpl
import com.example.spaceflightnews.util.BaseFragment
import com.example.spaceflightnews.util.DateUtil.formatDate
import com.example.spaceflightnews.viewModel.DetailViewModel

class DetailFragment : BaseFragment<FragmentDetailBinding>(FragmentDetailBinding::inflate) {
    private lateinit var article: Article
    private val args: DetailFragmentArgs by navArgs()
    private lateinit var viewModel: DetailViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        article = args.article
        initViewModel()
        viewModel.isArticleFavorited(args.article.id)
        setupUI()

    }

    private fun initViewModel() {
        val api = SpaceflightApp.retrofit.create(SpaceflightApiService::class.java)
        val dao = SpaceflightApp.articleDao
        val repository = NewsRepositoryImpl(api, dao)
        
        // Initialize UseCases for Detail
        val updateFavoriteUseCase = UpdateFavoriteUseCase(repository)
        val isArticleFavoritedUseCase = IsArticleFavoritedUseCase(repository)
        
        viewModel = DetailViewModel(
            updateFavoriteUseCase,
            isArticleFavoritedUseCase
        )
    }

    private fun setupUI() {
        binding.apply {
            detailTitle.text = article.title
            detailSummary.text = article.summary
            detailDate.text = formatDate(article.publishedAt)

            Glide.with(requireContext())
                .load(article.imageUrl)
                .error(R.drawable.default_space)
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        imgProgressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        imgProgressBar.visibility = View.GONE
                        return false
                    }

                })
                .into(imgNewsDetail)



            btnFavorite.setOnClickListener {
                val currentStatus = viewModel.isFavorite.value ?: false
                val newStatus = !currentStatus
                viewModel.updateFavorite(article.id, newStatus)
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
            imgBack.setOnClickListener {
                findNavController().navigateUp()
            }

        }

    }
}