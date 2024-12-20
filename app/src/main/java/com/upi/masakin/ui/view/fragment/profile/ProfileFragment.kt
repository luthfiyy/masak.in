package com.upi.masakin.ui.view.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.upi.masakin.R
import com.upi.masakin.databinding.FragmentProfileBinding
import com.upi.masakin.ui.view.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setupUserProfile()
        setupButtons()
    }

    private fun setupUserProfile() {
        // Get current user from Firebase
        firebaseAuth.currentUser?.let { user ->
            // Set email
            binding.tvEmail.text = user.email

            // Set name (if available)
            binding.tvName.text = user.displayName ?: getString(R.string.name)

            // You can also load profile picture if user has one
//            user.photoUrl?.let { photoUrl ->
                // Use Glide to load profile picture
                // Glide.with(this)
                //     .load(photoUrl)
                //     .placeholder(R.drawable.img_profile_picture)
                //     .error(R.drawable.img_profile_picture)
                //     .into(binding.cvProfile)
//            }
        }
    }

    private fun setupButtons() {
        // Edit Profile button
        binding.btnEditProfile.setOnClickListener {
            // TODO: Implement edit profile functionality
            // Launch edit profile activity/dialog
        }

        // Settings button
        binding.btnSettings.setOnClickListener {
            // Show settings options
            showSettingsOptions()
        }
    }

    private fun showSettingsOptions() {
        // Create settings menu with logout option
        val settingsDialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.settings))
            .setItems(arrayOf("Logout")) { _, which ->
                when (which) {
                    0 -> logout()
                }
            }
            .create()

        settingsDialog.show()
    }

    private fun logout() {
        firebaseAuth.signOut()
        // Navigate to login screen
        Intent(requireContext(), LoginActivity::class.java).also { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}