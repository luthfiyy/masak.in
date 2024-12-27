package com.upi.masakin.ui.view.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.upi.masakin.R
import com.upi.masakin.databinding.FragmentProfileBinding
import com.upi.masakin.ui.view.LoginActivity
import com.upi.masakin.ui.view.RegisterActivity
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

        if (firebaseAuth.currentUser == null) {
            navigateToLoginScreen()
            return
        }

        setupUserProfile()
        setupButtons()
    }

    private fun navigateToLoginScreen() {
        // Logout anonymous user first if exists
        if (firebaseAuth.currentUser?.isAnonymous == true) {
            firebaseAuth.signOut()
        }

        Intent(requireContext(), LoginActivity::class.java).also { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun setupUserProfile() {
        val currentUser = firebaseAuth.currentUser

        when {
            currentUser == null -> {
                handleNotLoggedInUser()
            }
            currentUser.isAnonymous -> {
                handleAnonymousUser()
            }
            else -> {
                handleRegularUser(currentUser)
            }
        }
    }

    private fun handleNotLoggedInUser() {
        binding.apply {
            tvEmail.text = getString(R.string.please_login)
            tvName.text = ""
            emailCard.visibility = View.GONE
            btnEditProfile.visibility = View.GONE
            btnSettings.text = getString(R.string.login)
            btnSettings.setOnClickListener { navigateToLoginScreen() }
        }
    }

    private fun handleAnonymousUser() {
        binding.apply {
            tvEmail.text = getString(R.string.guest_user)
            tvName.text = getString(R.string.guest)
            emailCard.visibility = View.VISIBLE
            btnEditProfile.visibility = View.GONE
            btnSettings.text = getString(R.string.login_or_register)
            btnSettings.setOnClickListener { showGuestOptions() }
        }
    }

    private fun handleRegularUser(user: FirebaseUser) {
        binding.apply {
            tvEmail.text = user.email
            tvName.text = user.displayName ?: getString(R.string.name)
            emailCard.visibility = View.VISIBLE
            btnEditProfile.visibility = View.VISIBLE
            btnSettings.text = getString(R.string.settings)
            btnSettings.setOnClickListener { showSettingsOptions() }
        }
    }


    private fun showGuestOptions() {
        val options = arrayOf(
            getString(R.string.login_with_email),
            getString(R.string.register_new_account),
            getString(R.string.logout)
        )

        android.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.guest_options))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        firebaseAuth.signOut() // Logout first
                        navigateToLoginScreen()
                    }
                    1 -> {
                        firebaseAuth.signOut() // Logout first
                        Intent(requireContext(), RegisterActivity::class.java).also {
                            startActivity(it)
                        }
                    }
                    2 -> logout()
                }
            }
            .show()
    }

    private fun setupButtons() {
        binding.btnEditProfile.setOnClickListener {
            // TODO: Implement edit profile functionality
        }

        binding.btnSettings.setOnClickListener {
            if (firebaseAuth.currentUser?.isAnonymous == true) {
                showGuestOptions()
            } else {
                showSettingsOptions()
            }
        }
    }

    private fun showSettingsOptions() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.settings))
            .setItems(arrayOf("Logout")) { _, which ->
                when (which) {
                    0 -> logout()
                }
            }
            .show()
    }

    private fun logout() {
        firebaseAuth.signOut()
        navigateToLoginScreen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}