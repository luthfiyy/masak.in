package com.upi.masakin.ui.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.upi.masakin.R
import com.upi.masakin.databinding.ActivitySplashScreenBinding
import com.upi.masakin.ui.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_Masakin_Splash)

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videoPath = "android.resource://$packageName/${R.raw.masakin}"
        binding.videoView.apply {
            setVideoURI(Uri.parse(videoPath))
            setOnPreparedListener { mediaPlayer ->
                mediaPlayer.isLooping = true
            }
            start()
        }

        lifecycleScope.launch {
            delay(2500)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}