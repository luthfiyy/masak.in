package com.upi.masakin.ui.view.recipe

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.upi.masakin.R
import com.upi.masakin.adapters.recipe.IngredientAdapter
import com.upi.masakin.data.entities.RecipeEntity
import com.upi.masakin.databinding.ActivityRecipeDetailBinding
import com.upi.masakin.ui.view.fragment.recipe.IngredientsFragment
import com.upi.masakin.ui.view.fragment.recipe.StepsFragment
import timber.log.Timber

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
            tvDetailTime.text = recipe.time
            tvDetailServing.text = recipe.serving
            rbItemRating.rating = recipe.rating
            tvReviews.text = recipe.reviews
            Glide.with(this@RecipeDetailActivity).load(recipe.image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.placeholder_article).into(imgDetailPhoto)
        }

        setupLikeButton(recipe)

        val rvIngredients = binding.rvIngredients
        rvIngredients.layoutManager = LinearLayoutManager(this)
        rvIngredients.adapter = IngredientAdapter(
            recipe.ingredients,
            recipe.ingredientImages
        )

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
                    0 -> {
                        IngredientsFragment.newInstance(
                            recipe.ingredients,
                            recipe.ingredientImages
                        )
                    }
                    1 -> StepsFragment.newInstance(
                        recipe.steps
                    )
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

    // Di RecipeDetailActivity, tambahkan logging di setupLikeButton:
    private fun setupLikeButton(recipe: RecipeEntity) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Debug user state
        Timber.d( "=== User Debug ===")
        Timber.d( "Current user: ${currentUser?.uid}")
        Timber.d( "Is anonymous: ${currentUser?.isAnonymous}")

        if (currentUser == null || currentUser.isAnonymous) {
            Timber.d( "User not logged in or anonymous")
            binding.fabLike.setOnClickListener {
                Toast.makeText(
                    this@RecipeDetailActivity,
                    "Please login to save favorite recipes",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return
        }

        val database = FirebaseDatabase.getInstance("https://masakin-76b91-default-rtdb.asia-southeast1.firebasedatabase.app")
        val path = "users/${currentUser.uid}/favorite_recipes"
        val userId = currentUser.uid
        val favoriteRecipesRef = database.getReference("users/$userId/favorite_recipes/${recipe.id}")

        // Debug database reference
        Timber.d( "=== Database Reference Debug ===")
        Timber.d( "Reference path: ${favoriteRecipesRef.path}")
        Timber.d( "Recipe ID: ${recipe.id}")

        database.getReference(path).get()
            .addOnSuccessListener { snapshot ->
                Timber.d("Raw data at path: $path")
                Timber.d("Snapshot exists: ${snapshot.exists()}")
                Timber.d("Snapshot value: ${snapshot.value}")
                Timber.d("Children count: ${snapshot.childrenCount}")
                snapshot.children.forEach { child ->
                    Timber.d("Child key: ${child.key}")
                    Timber.d("Child value: ${child.value}")
                }
            }
            .addOnFailureListener { e ->
                Timber.e("Failed to read data: ${e.message}")
            }

        favoriteRecipesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Debug snapshot
                Timber.d( "=== Like Status Debug ===")
                Timber.d( "Snapshot exists: ${snapshot.exists()}")
                Timber.d( "Snapshot value: ${snapshot.value}")

                isLiked = snapshot.exists()
                updateLikeButtonState()
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e( "=== Error Debug ===")
                Timber.e( "Error checking favorite status: ${error.message}")
                Timber.e( "Error details: ${error.details}")
            }
        })

        binding.fabLike.setOnClickListener {
            // Debug like action
            Timber.d( "=== Like Action Debug ===")
            Timber.d( "Current like status: $isLiked")
            Timber.d( "Recipe to save: ${recipe.title}")

            if (isLiked) {
                favoriteRecipesRef.removeValue()
                    .addOnSuccessListener {
                        Timber.d( "Successfully removed from favorites")
                        isLiked = false
                        updateLikeButtonState()
                        Toast.makeText(
                            this@RecipeDetailActivity,
                            "Removed from favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Timber.e( "Error removing from favorites", e)
                        Toast.makeText(
                            this@RecipeDetailActivity,
                            "Error: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
            // Buat salinan resep dengan favorite = true
            val recipeToSave = recipe.copy(isFavorite = true)

            favoriteRecipesRef.setValue(recipeToSave)
                .addOnSuccessListener {
                    Timber.d( "Successfully added to favorites")
                    isLiked = true
                    updateLikeButtonState()
                    Toast.makeText(
                        this@RecipeDetailActivity,
                        "Added to favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Timber.e( "Error adding to favorites", e)
                    Toast.makeText(
                        this@RecipeDetailActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
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