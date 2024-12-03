package com.upi.masakin.adapters.article

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.upi.masakin.R
import com.upi.masakin.databinding.ItemArticleBinding
import com.upi.masakin.data.entities.Article

class ArticleAdapter(
    private val artikels: MutableList<Article>,
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
        val artikel = artikels[position]
        holder.binding.apply {
            tvArtikelTitle.text = artikel.title
            tvArtikelAuthor.text = artikel.author
            tvArtikelCategory.text = artikel.category.toString()
            tvArtikelTime.text = artikel.readTime.toString()
            tvArticleShortDescription.text = artikel.shortDescription

            Glide.with(root.context)
                .load(artikel.thumbnailUrl)
                .placeholder(R.drawable.placeholder_artikel)
                .error(R.drawable.placeholder_artikel)
                .into(ivArtikelThumbnail)

            root.setOnClickListener { onItemClick(artikel) }
        }
    }

    override fun getItemCount() = artikels.size

    fun updateData(newArticles: List<Article>) {
        artikels.clear()
        artikels.addAll(newArticles)
        notifyDataSetChanged()
    }

    class ArticleViewHolder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)
}
