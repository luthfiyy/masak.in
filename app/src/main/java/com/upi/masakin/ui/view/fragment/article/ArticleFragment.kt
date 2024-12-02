package com.upi.masakin.ui.view.fragment.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upi.masakin.adapters.ArticleAdapter
import com.upi.masakin.data.database.MasakinDatabase
import com.upi.masakin.data.repository.ArticleRepository
import com.upi.masakin.databinding.FragmentArticleBinding
import com.upi.masakin.ui.viewmodel.ArticleViewModel
import kotlinx.coroutines.launch

class ArticleFragment : Fragment() {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!

    private lateinit var articleAdapter: ArticleAdapter
    private val viewModel: ArticleViewModel by viewModels {
        val database = MasakinDatabase.getDatabase(requireContext())
        val repository = ArticleRepository(database.articleDao())
        ArticleViewModel.ArticleViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchBar.addTextChangedListener { text ->
            val query = text.toString().trim()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.searchArticle(query).collect { filteredRecipes ->
                    articleAdapter.updateData(filteredRecipes)
                }
            }
        }


        setupRecyclerView()
        observeArticles()
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(mutableListOf()) { article ->
            navigateToArticleDetail(article)
        }
        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = articleAdapter
        }
    }

    private fun observeArticles() {
        lifecycleScope.launch {
            viewModel.articles.collect { articles ->
                articleAdapter.updateData(articles)
            }
        }
    }

    private fun navigateToArticleDetail(article: com.upi.masakin.data.entities.Article) {
        val action = ArticleFragmentDirections.actionArticleFragmentToArticleDetailFragment(article)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
