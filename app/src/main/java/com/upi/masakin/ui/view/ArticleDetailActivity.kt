package com.upi.masakin.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.upi.masakin.R
import com.upi.masakin.databinding.ActivityArticleDetailBinding

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args: ArticleDetailActivityArgs by navArgs()
        val article = args.article

        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            Glide.with(this@ArticleDetailActivity)
                .load(article.thumbnailUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.placeholder_article)
                .into(ivArticleImage)

            tvArticleTitle.text = article.title
            tvAuthor.text = article.author
            tvPublishDate.text = article.publishDate
            tvReadTime.text = getString(R.string.read_time_format, article.readTime)
            tvShortDescription.text = article.shortDescription
            tvArticleContent.text = article.content
            tvCategory.text = article.category.toReadableString()
            tvTags.text = article.tags.joinToString(", ")
        }
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
    }

}
