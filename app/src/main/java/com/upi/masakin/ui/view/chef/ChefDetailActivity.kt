package com.upi.masakin.ui.view.chef

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.upi.masakin.adapters.recipe.ListRecipeAdapter
import com.upi.masakin.databinding.ActivityChefDetailBinding
import com.upi.masakin.ui.view.recipe.RecipeDetailActivity
import com.upi.masakin.ui.viewmodel.chef.ChefViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChefDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChefDetailBinding
    private lateinit var recipeAdapter: ListRecipeAdapter

    private val chefViewModel: ChefViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChefDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val args: ChefDetailActivityArgs by navArgs()
        val chef = args.chef

        binding.tvChefName.text = chef.name
        binding.tvChefDescription.text = chef.description
        binding.imgChefProfile.setImageResource(chef.image)

        setupRecyclerView()
        observeRecipes(chef.id)
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

    private fun observeRecipes(chefId: Int) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                chefViewModel.getRecipesByChefId(chefId)
                    .collect { recipes ->
                        recipeAdapter.updateRecipes(recipes)
                    }
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
    }
}