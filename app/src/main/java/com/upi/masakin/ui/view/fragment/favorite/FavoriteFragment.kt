package com.upi.masakin.ui.view.fragment.favorite

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.upi.masakin.R
import com.upi.masakin.adapters.recipe.ListRecipeAdapter
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.databinding.FragmentFavoriteBinding
import timber.log.Timber

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var recipeAdapter: ListRecipeAdapter
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var gson: Gson
    private var favoriteRecipesListener: ValueEventListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)

        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)

        sharedPrefs =
            requireContext().getSharedPreferences("FavoriteRecipes", AppCompatActivity.MODE_PRIVATE)
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
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
        val isFakestoreLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)
        val fakestoreUserId = sharedPreferences.getString("user_id", null)

        if ((firebaseUser == null || firebaseUser.isAnonymous) && (!isFakestoreLoggedIn || fakestoreUserId == null)) {
            updateFavoriteViewVisibility(emptyList())
            binding.emptyText.text = getString(R.string.login_to_see_favorites)
            return
        }

        // Load Firebase favorites
        if (firebaseUser != null && !firebaseUser.isAnonymous) {
            loadFirebaseFavorites(firebaseUser.uid)
        }
        // Load Fakestore favorites
        else if (isFakestoreLoggedIn && fakestoreUserId != null) {
            loadFakestoreFavorites(fakestoreUserId)
        }
    }

    private fun loadFirebaseFavorites(userId: String) {
        val database = FirebaseDatabase.getInstance("https://masakin-76b91-default-rtdb.asia-southeast1.firebasedatabase.app")
        val favoriteRecipesRef = database.getReference("users/$userId/favorite_recipes")

        favoriteRecipesListener = favoriteRecipesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoriteRecipes = mutableListOf<RecipeEntity>()
                for (recipeSnapshot in snapshot.children) {
                    recipeSnapshot.getValue(RecipeEntity::class.java)?.let { recipe ->
                        favoriteRecipes.add(recipe)
                    }
                }
                updateFavoriteViewVisibility(favoriteRecipes)
                recipeAdapter.updateRecipes(favoriteRecipes)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error loading favorites: ${error.message}")
                context?.let {
                    Toast.makeText(it, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun loadFakestoreFavorites(userId: String) {
        val favoritesPrefs = requireContext().getSharedPreferences("favorite_recipes_$userId", AppCompatActivity.MODE_PRIVATE)
        val recipesPrefs = requireContext().getSharedPreferences("recipes_data_$userId", AppCompatActivity.MODE_PRIVATE)
        val favoriteRecipes = mutableListOf<RecipeEntity>()

        favoritesPrefs.all.forEach { (recipeId, isFavorite) ->
            if (isFavorite as Boolean) {
                val recipeJson = recipesPrefs.getString(recipeId, null)
                if (recipeJson != null) {
                    try {
                        val recipe = Gson().fromJson(recipeJson, RecipeEntity::class.java)
                        favoriteRecipes.add(recipe)
                    } catch (e: Exception) {
                        Timber.e("Error parsing recipe: $e")
                    }
                }
            }
        }

        updateFavoriteViewVisibility(favoriteRecipes)
        recipeAdapter.updateRecipes(favoriteRecipes)
    }
    private fun updateFavoriteViewVisibility(favoriteRecipes: List<RecipeEntity>) {
        _binding?.apply {
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

    private val favoriteRecipesRef by lazy {
        FirebaseDatabase.getInstance("https://masakin-76b91-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("users/${FirebaseAuth.getInstance().currentUser?.uid}/favorite_recipes")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        favoriteRecipesListener?.let { favoriteRecipesRef.removeEventListener(it) }
        _binding = null
    }

    private fun navigateToRecipeDetail(recipe: RecipeEntity) {
        val action = FavoriteFragmentDirections.actionFavoriteToDetail(recipe)
        findNavController().navigate(action)
    }
}