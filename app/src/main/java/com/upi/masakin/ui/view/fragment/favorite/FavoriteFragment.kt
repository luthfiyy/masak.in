package com.upi.masakin.ui.view.fragment.favorite

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import com.upi.masakin.R
import com.upi.masakin.adapters.recipe.ListRecipeAdapter
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var recipeAdapter: ListRecipeAdapter
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var gson: Gson

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)

        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)

        sharedPrefs = requireContext().getSharedPreferences("FavoriteRecipes", AppCompatActivity.MODE_PRIVATE)
        gson = Gson()

        val menuHost: MenuHost = requireActivity()
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_list -> {
                        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
                        true
                    }
                    R.id.action_grid -> {
                        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
                        true
                    }
                    else -> false
                }
            }
        }
        menuHost.removeMenuProvider(menuProvider)
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner)

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadFavoriteRecipes()
    }

    private fun setupRecyclerView() {
        recipeAdapter = ListRecipeAdapter(arrayListOf()) { recipe ->
            navigateToRecipeDetail(recipe)
        }
        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFavorites.adapter = recipeAdapter
    }

    private fun loadFavoriteRecipes() {
        val existingData = sharedPrefs.getString("favorite_recipes", "[]")
        val type = getType<List<RecipeEntity>>()
        val favoriteRecipes: List<RecipeEntity> = gson.fromJson(existingData, type) ?: listOf()

        updateFavoriteViewVisibility(favoriteRecipes)
        recipeAdapter.updateRecipes(favoriteRecipes)
    }

    private fun updateFavoriteViewVisibility(favoriteRecipes: List<RecipeEntity>) {
        binding.apply {
            if (favoriteRecipes.isEmpty()) {
                rvFavorites.visibility = View.GONE
                emptyImage.visibility = View.VISIBLE
                emptyText.visibility = View.VISIBLE
            } else {
                rvFavorites.visibility = View.VISIBLE
                emptyImage.visibility = View.GONE
                emptyText.visibility = View.GONE
            }
        }
    }

    private inline fun <reified T> getType(): Type {
        return object : TypeToken<T>() {}.type
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToRecipeDetail(recipe: RecipeEntity) {
        val action = FavoriteFragmentDirections.actionFavoriteToDetail(recipe)
        findNavController().navigate(action)
    }
}