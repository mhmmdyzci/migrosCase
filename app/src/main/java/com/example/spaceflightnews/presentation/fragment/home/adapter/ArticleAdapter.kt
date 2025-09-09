package com.example.spaceflightnews.presentation.fragment.home.adapter


import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.spaceflightnews.R
import com.example.spaceflightnews.databinding.ItemArticleBinding
import com.example.spaceflightnews.domain.model.Article
import com.example.spaceflightnews.util.DateUtil.formatDate

class ArticleAdapter(
    context: Context,
    private val onItemClick: (Article) -> Unit
) : ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffCallback()) {

    private val glide: RequestManager = Glide.with(context)

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.apply {
                articleTitle.text = article.title
                articleSummary.text = article.summary
                articleDate.text = formatDate(article.publishedAt)


                imgProgressBar.visibility = View.VISIBLE
                
                glide
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
                    .into(imgNews)


                binding.root.setOnClickListener {
                    onItemClick(article)
                }
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    override fun onViewRecycled(holder: ArticleViewHolder) {
        super.onViewRecycled(holder)
        val imageView = holder.itemView.findViewById<ImageView>(R.id.img_news)
        if (imageView != null) {
            glide.clear(imageView)
        }
    }
}

class ArticleDiffCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}
