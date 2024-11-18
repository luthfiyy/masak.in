package com.upi.masakin.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.upi.masakin.R
import com.upi.masakin.databinding.ActivityRecipeDetailBinding
import com.upi.masakin.model.Recipe

class RecipeDetailActivity : AppCompatActivity() {
    private var recipeTitle: String? = null
    private var recipeIngredients: String? = null
    private var recipeSteps: String? = null
    private var recipeDescription: String? = null
    private var recipeId: Int = -1
    private var isLiked: Boolean = false

    private lateinit var binding: ActivityRecipeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val recipe = intent.getParcelableExtra<Recipe>("recipe")
        recipe?.let {
            with(binding) {
                recipeTitle = it.title
                recipeIngredients = it.ingredients
                recipeDescription = it.description
                recipeSteps = it.steps

                tvDetailTitle.text = it.title
                tvDetailIngredients.text = it.ingredients
                tvDetailDescription.text = it.description
                tvDetailTime.text = it.time
                tvDetailSteps.text = it.steps
                tvDetailServing.text = it.serving
                imgDetailPhoto.setImageResource(it.image)
            }

            setupLikeButton()
        }
    }

    private fun setupLikeButton() {
        val sharedPrefs = getSharedPreferences("RecipeLikes", MODE_PRIVATE)
        isLiked = sharedPrefs.getBoolean("recipe_$recipeId", false)

        updateLikeButtonState()

        binding.fabLike.setOnClickListener {
            isLiked = !isLiked

            sharedPrefs.edit().putBoolean("recipe_$recipeId", isLiked).apply()

            updateLikeButtonState()
        }
    }

    private fun updateLikeButtonState() {
        val iconResource = if (isLiked) {
            R.drawable.ic_favorite
        } else {
            R.drawable.ic_favorite_border
        }

        binding.fabLike.setImageDrawable(
            ContextCompat.getDrawable(this, iconResource)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            R.id.action_share -> {
                shareRecipe()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareRecipe() {
        val shareText = buildString {
            append("$recipeTitle\n\n")
            append("Deskripsi:\n$recipeDescription\n\n")
            append("Bahan-bahan:\n$recipeIngredients\n\n")
            append("Cara Membuat:\n$recipeSteps")
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(shareIntent, "Bagikan Resep"))
    }
}