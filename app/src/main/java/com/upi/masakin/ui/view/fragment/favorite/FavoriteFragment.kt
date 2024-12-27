package com.upi.masakin.ui.view.fragment.favorite

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d(
            "FavoriteFragment",
            "Current user: ${currentUser?.uid}, isAnonymous: ${currentUser?.isAnonymous}"
        )

        if (currentUser == null || currentUser.isAnonymous) {
            updateFavoriteViewVisibility(emptyList())
            binding.emptyText.text = getString(R.string.login_to_see_favorites)
            return
        }

        val database =
            FirebaseDatabase.getInstance("https://masakin-76b91-default-rtdb.asia-southeast1.firebasedatabase.app")
        val path = "users/${currentUser.uid}/favorite_recipes"
        Log.d("FavoriteFragment", "Attempting to load from path: $path")
        val favoriteRecipesRef = database.getReference(path)
        Log.d("FavoriteFragment", "Database Reference: ${favoriteRecipesRef.ref}")


        // Add immediate value check
        favoriteRecipesRef.get().addOnSuccessListener { snapshot ->
            Log.d("FavoriteFragment", "Direct snapshot check - exists: ${snapshot.exists()}")
            Log.d("FavoriteFragment", "Direct snapshot check - value: ${snapshot.value}")
        }.addOnFailureListener { exception ->
            Log.e("FavoriteFragment", "Error getting data", exception)
        }

        favoriteRecipesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FavoriteFragment", "Snapshot exists: ${snapshot.exists()}")
                Log.d("FavoriteFragment", "Snapshot children count: ${snapshot.childrenCount}")
                Log.d("FavoriteFragment", "Raw snapshot value: ${snapshot.value}")
                Log.d("FavoriteFragment", "Snapshot children: ${snapshot.children.map { it.key }}")

                val favoriteRecipes = mutableListOf<RecipeEntity>()

                for (recipeSnapshot in snapshot.children) {
                    try {
                        recipeSnapshot.getValue(RecipeEntity::class.java)?.let { recipe ->
                            Log.d("FavoriteFragment", "Recipe loaded: ${recipe.title}")
                            favoriteRecipes.add(recipe)
                        } ?: Log.e(
                            "FavoriteFragment",
                            "Failed to convert snapshot: ${recipeSnapshot.value}"
                        )
                    } catch (e: Exception) {
                        Log.e("FavoriteFragment", "Error parsing recipe: ${e.message}")
                        Log.e("FavoriteFragment", "Snapshot value: ${recipeSnapshot.value}")
                    }
                }

                Log.d("FavoriteFragment ", "Total recipes loaded: ${favoriteRecipes.size}")
                updateFavoriteViewVisibility(favoriteRecipes)
                recipeAdapter.updateRecipes(favoriteRecipes)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FavoriteFragment", "Error loading favorites: ${error.message}")
                Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

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