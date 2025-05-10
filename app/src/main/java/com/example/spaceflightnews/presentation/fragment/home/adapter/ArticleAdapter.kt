package com.example.spaceflightnews.presentation.fragment.home.adapter


import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceflightnews.databinding.ItemArticleBinding
import com.example.spaceflightnews.domain.model.Article
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private val articles = mutableListOf<Article>()

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.articleTitle.text = article.title
            binding.articleSummary.text = article.summary
            binding.articleDate.text = formatDate(article.publishedAt)
        }

        private fun formatDate(isoDate: String): String {
            return try {
                val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val sdfOutput = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                val date = sdfInput.parse(isoDate.replace("Z", "").substring(0, 19))
                sdfOutput.format(date!!)
            } catch (e: Exception) {
                isoDate
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun getItemCount(): Int = articles.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    fun submitList(newList: List<Article>) {
        articles.clear()
        articles.addAll(newList)
        notifyDataSetChanged()
    }
}
