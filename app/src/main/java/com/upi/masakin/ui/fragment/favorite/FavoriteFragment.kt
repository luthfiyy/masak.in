package com.upi.masakin.ui.fragment.favorite

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken // Tambahkan ini
import java.lang.reflect.Type // Tambahkan ini
import com.upi.masakin.R
import com.upi.masakin.adapters.ListRecipeAdapter
import com.upi.masakin.databinding.FragmentFavoriteBinding
import com.upi.masakin.model.Recipe

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var recipeAdapter: ListRecipeAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)

        loadFavoriteRecipes()
    }

    private fun loadFavoriteRecipes() {
        val sharedPrefs = requireContext().getSharedPreferences("FavoriteRecipes", AppCompatActivity.MODE_PRIVATE)
        val gson = Gson()

        val existingData = sharedPrefs.getString("favorite_recipes", "[]")
        val type = getType<List<Recipe>>()
        val favoriteRecipes: List<Recipe> = gson.fromJson(existingData, type) ?: listOf()

        if (favoriteRecipes.isEmpty()) {
            binding.apply {
                recyclerViewFavorites.visibility = View.GONE
                emptyImage.visibility = View.VISIBLE
                emptyText.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                recyclerViewFavorites.visibility = View.VISIBLE
                emptyImage.visibility = View.GONE
                emptyText.visibility = View.GONE
            }
        }

        setupRecyclerView(favoriteRecipes)
    }

    private inline fun <reified T> getType(): Type {
        return object : TypeToken<T>() {}.type
    }

    private fun setupRecyclerView(recipes: List<Recipe>) {
        recipeAdapter = ListRecipeAdapter(ArrayList(recipes)) { recipe ->
            navigateToRecipeDetail(recipe)
        }

        binding.recyclerViewFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipeAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToRecipeDetail(recipe: Recipe) {
        val action = FavoriteFragmentDirections.actionFavoriteToDetail(recipe)
        findNavController().navigate(action)
    }

}
