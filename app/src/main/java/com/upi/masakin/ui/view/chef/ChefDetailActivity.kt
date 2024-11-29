package com.upi.masakin.ui.view.chef

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.upi.masakin.adapters.ListRecipeAdapter
import com.upi.masakin.data.database.MasakinDatabase
import com.upi.masakin.databinding.ActivityChefDetailBinding
import com.upi.masakin.ui.view.recipe.RecipeDetailActivity
import kotlinx.coroutines.launch

class ChefDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChefDetailBinding
    private lateinit var recipeAdapter: ListRecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChefDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val args by navArgs<ChefDetailActivityArgs>()
        val chef = args.chef

        binding.tvChefName.text = chef.name
        binding.tvChefDescription.text = chef.description
        binding.imgChefProfile.setImageResource(chef.image)

        setupRecyclerView()
        loadRecipes(chef.id)
        setupListeners()
    }

    private fun setupRecyclerView() {
        recipeAdapter = ListRecipeAdapter(arrayListOf()) { recipe ->
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("recipe", recipe)
            }
            startActivity(intent)
        }

        binding.rvRecipeList.apply {
            layoutManager = GridLayoutManager(this@ChefDetailActivity, 2)
            adapter = recipeAdapter
        }
    }


    private fun loadRecipes(chefId: Int) {
        val dao = MasakinDatabase.getDatabase(this).chefDao()
        lifecycleScope.launch {
            val recipes = dao.getRecipesByChefId(chefId)
            recipeAdapter.updateRecipes(recipes)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
    }
}
