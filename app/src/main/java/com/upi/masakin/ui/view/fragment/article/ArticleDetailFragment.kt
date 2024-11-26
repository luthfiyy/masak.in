package com.upi.masakin.ui.view.fragment.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.upi.masakin.R
import com.upi.masakin.databinding.FragmentDetailArticleBinding

class ArticleDetailFragment : Fragment() {
    private var _binding: FragmentDetailArticleBinding? = null
    private val binding get() = _binding!!

    private val args: ArticleDetailFragmentArgs by navArgs()
    private var isLiked = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val article = args.article

        with(binding) {
            // Load image using Glide
            Glide.with(requireContext())
                .load(article.thumbnailUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.placeholder_artikel)
                .into(ivArticleImage)

            // Set text content
            tvArticleTitle.text = article.title
            tvAuthor.text = article.author
            tvPublishDate.text = article.publishDate
            tvReadTime.text = getString(R.string.read_time_format, article.readTime)
            tvShortDescription.text = article.shortDescription
            tvArticleContent.text = article.content

            // Set category and tags
            tvCategory.text = article.category.toReadableString()
            tvTags.text = article.tags.joinToString(", ")
        }
    }

    private fun setupListeners() {
        with(binding) {
            // Back button click listener
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            // Favorite button click listener
            fabLike.setOnClickListener {
                isLiked = !isLiked
                updateFavoriteButton()
            }
        }
    }

    private fun updateFavoriteButton() {
        binding.fabLike.setImageResource(
            if (isLiked) R.drawable.ic_favorite
            else R.drawable.ic_favorite_border
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}