package com.upi.masakin.adapters.article

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upi.masakin.R
import com.upi.masakin.databinding.ItemArticleBinding
import com.upi.masakin.data.entities.Article
import com.upi.masakin.utils.DiffUtilCallback

class ArticleAdapter(
    private val articles: MutableList<Article>,
    private val onItemClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.binding.apply {
            tvArticleTitle.text = article.title
            tvArticleAuthor.text = article.author
            tvArticleCategory.text = article.category.toString()
            tvArticleTime.text = root.context.getString(R.string.read_time_format, article.readTime)
            tvArticleShortDescription.text = article.shortDescription

            Glide.with(root.context)
                .load(article.thumbnailUrl)
                .placeholder(R.drawable.placeholder_article)
                .error(R.drawable.placeholder_article)
                .into(ivArticleThumbnail)

            root.setOnClickListener { onItemClick(article) }
        }
    }

    override fun getItemCount() = articles.size

    fun updateData(newArticles: List<Article>) {
        val diffCallback = DiffUtilCallback(
            oldList = articles,
            newList = newArticles,
            areItemsTheSame = { oldItem, newItem -> oldItem.id == newItem.id },
            areContentsTheSame = { oldItem, newItem -> oldItem == newItem }
        )
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        articles.clear()
        articles.addAll(newArticles)
        diffResult.dispatchUpdatesTo(this)
    }

    class ArticleViewHolder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)
}
