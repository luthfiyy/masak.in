package com.upi.masakin.ui.view.recipe

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.upi.masakin.R
import com.upi.masakin.adapters.recipe.RecipeStepsAdapter
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.databinding.ActivityRecipeDetailBinding

class RecipeDetailActivity : AppCompatActivity() {
    private var recipeTitle: String? = null
    private var recipeIngredients: String? = null
    private var recipeSteps: String? = null
    private var recipeDescription: String? = null
    private var isLiked: Boolean = false
    private lateinit var btnIngredients: Button
    private lateinit var btnSteps: Button
    private lateinit var ingredientsSection: LinearLayout
    private lateinit var stepsAdapter: RecipeStepsAdapter
    private lateinit var binding: ActivityRecipeDetailBinding
    private var isVideoPlaying = false
    private var youTubePlayer: YouTubePlayer? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val args: RecipeDetailActivityArgs by navArgs()
        val recipe = args.recipe

        btnIngredients = binding.btnIngredients
        btnSteps = binding.btnSteps
        ingredientsSection = binding.ingredientsSection

        binding.btnBack.setOnClickListener {
            finish()
        }

        with(binding) {
            tvDetailTitle.text = recipe.title
            tvDetailIngredients.text = recipe.ingredients.joinToString("\n")
            tvDetailTime.text = recipe.time
            tvDetailServing.text = recipe.serving
            rbItemRating.rating = recipe.rating
            tvReviews.text = recipe.reviews
            imgDetailPhoto.setImageResource(recipe.image)
        }

        btnSteps.setOnClickListener {
            btnSteps.isSelected = true
            btnIngredients.isSelected = false
        }

        btnIngredients.setOnClickListener {
            btnSteps.isSelected = false
            btnIngredients.isSelected = true
        }

        btnSteps.isSelected = true
        setupLikeButton(recipe)

        setupRecyclerView(recipe.steps)

        btnIngredients.setOnClickListener { showBahanContent() }
        btnSteps.setOnClickListener { showCaraMasakContent() }

        val youtubePlayerView = binding.youtubePlayerView
        lifecycle.addObserver(youtubePlayerView)

        binding.btnPlay.setOnClickListener {
            if (!isVideoPlaying) {
                binding.youtubePlayerView.visibility = View.VISIBLE
                youTubePlayer?.play()
                isVideoPlaying = true
                binding.btnPlay.setImageResource(R.drawable.ic_stop)
            } else {
                youTubePlayer?.pause()
                binding.youtubePlayerView.visibility = View.GONE
                isVideoPlaying = false
                binding.btnPlay.setImageResource(R.drawable.ic_play)
            }
        }

        binding.youtubePlayerView.addYouTubePlayerListener(object :
            AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                this@RecipeDetailActivity.youTubePlayer = youTubePlayer
                val videoId = recipe.videoId
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })
        showBahanContent()
    }

    private fun showBahanContent() {
        ingredientsSection.visibility = View.VISIBLE
        binding.stepsRecyclerView.visibility = View.GONE

        btnIngredients.setBackgroundTintList(
            ContextCompat.getColorStateList(
                this, R.color.button_active
            )
        )
        btnSteps.setBackgroundTintList(
            ContextCompat.getColorStateList(
                this, R.color.button_inactive
            )
        )

        btnIngredients.setTextColor(ContextCompat.getColor(this, R.color.button_text_active))
        btnSteps.setTextColor(ContextCompat.getColor(this, R.color.button_text_inactive))
    }

    private fun showCaraMasakContent() {
        ingredientsSection.visibility = View.GONE
        binding.stepsRecyclerView.visibility = View.VISIBLE

        btnIngredients.setBackgroundTintList(
            ContextCompat.getColorStateList(
                this, R.color.button_inactive
            )
        )
        btnSteps.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.button_active))

        btnIngredients.setTextColor(ContextCompat.getColor(this, R.color.button_text_inactive))
        btnSteps.setTextColor(ContextCompat.getColor(this, R.color.button_text_active))
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setupLikeButton(recipe: RecipeEntity) {
        val sharedPrefs = getSharedPreferences("FavoriteRecipes", MODE_PRIVATE)
        val gson = Gson()

        val favoritesJson = sharedPrefs.getString("favorite_recipes", "[]")
        val type = object : TypeToken<ArrayList<RecipeEntity>>() {}.type
        val favoriteList =
            gson.fromJson<ArrayList<RecipeEntity>>(favoritesJson, type) ?: ArrayList()

        isLiked = favoriteList.any { it.id == recipe.id }
        updateLikeButtonState()

        binding.fabLike.setOnClickListener {
            if (isLiked) {
                favoriteList.removeIf { it.id == recipe.id }

                // Set a result for the FavoriteFragment to reload
                supportFragmentManager.setFragmentResult("recipe_unliked_key", Bundle())
            } else {
                favoriteList.add(recipe)
            }

            val editor = sharedPrefs.edit()
            val updatedJson = gson.toJson(favoriteList)
            editor.putString("favorite_recipes", updatedJson)
            editor.apply()

            isLiked = !isLiked
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

    private fun setupRecyclerView(steps: List<String>) {
        stepsAdapter = RecipeStepsAdapter(steps)
        binding.stepsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@RecipeDetailActivity)
            adapter = stepsAdapter
            background = ContextCompat.getDrawable(this@RecipeDetailActivity, R.drawable.bg_rounded)
        }
    }

    override fun onPause() {
        super.onPause()
        youTubePlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayer = null
    }

}