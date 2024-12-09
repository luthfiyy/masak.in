package com.upi.masakin.ui.view.recipe

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.upi.masakin.R
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.databinding.ActivityRecipeDetailBinding
import com.upi.masakin.ui.view.fragment.recipe.IngredientsFragment
import com.upi.masakin.ui.view.fragment.recipe.StepsFragment

class RecipeDetailActivity : AppCompatActivity() {
    private var recipeTitle: String? = null
    private var recipeIngredients: String? = null
    private var recipeSteps: String? = null
    private var recipeDescription: String? = null
    private var isLiked: Boolean = false
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

        binding.ingredientsSection.visibility = View.GONE

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

        setupLikeButton(recipe)


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
        setupTabLayoutWithViewPager(recipe)

    }

    private fun setupTabLayoutWithViewPager(recipe: RecipeEntity) {
        val viewPagerAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> IngredientsFragment.newInstance(recipe.ingredients)
                    1 -> StepsFragment.newInstance(recipe.steps)
                    else -> throw IllegalArgumentException("Invalid position")
                }
            }
        }

        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Ingredients"
                1 -> "Steps"
                else -> ""
            }
        }.attach()
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


    override fun onPause() {
        super.onPause()
        youTubePlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayer = null
    }

}