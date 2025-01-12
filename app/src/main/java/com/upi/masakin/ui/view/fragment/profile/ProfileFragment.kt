package com.upi.masakin.ui.view.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.upi.masakin.R
import com.upi.masakin.data.api.auth.FakeStoreSessionManager
import com.upi.masakin.databinding.FragmentProfileBinding
import com.upi.masakin.ui.view.auth.LoginActivity
import com.upi.masakin.ui.view.auth.RegisterActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var isFakeStoreUser = true
    private var fakeStoreUsername: String? = null

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var fakeStoreSessionManager: FakeStoreSessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        isFakeStoreUser = fakeStoreSessionManager.isLoggedIn()
        fakeStoreUsername = fakeStoreSessionManager.getFakeStoreUsername()

        if (!isFakeStoreUser && !isFirebaseUserValid()) {
            Timber.d("Navigating to login - No auth user found")
            navigateToLoginScreen()
            return
        }

        setupUserProfile()
        setupButtons()
    }

    private fun isFirebaseUserValid(): Boolean {
        return firebaseAuth.currentUser != null
    }

    private fun setupUserProfile() {
        Timber.d("Setting up user profile - isFakeStoreUser: $isFakeStoreUser, isAnonymous: ${firebaseAuth.currentUser?.isAnonymous}")

        when {
            isFakeStoreUser -> {
                Timber.d("Handling FakeStore user with username: $fakeStoreUsername")
                handleFakeStoreUser()
            }
            firebaseAuth.currentUser?.isAnonymous == true -> {
                Timber.d("Handling anonymous user")
                handleAnonymousUser()
            }
            firebaseAuth.currentUser != null -> {
                Timber.d("Handling regular Firebase user: ${firebaseAuth.currentUser?.email}")
                handleRegularUser(firebaseAuth.currentUser!!)
            }
            else -> {
                Timber.d("Handling not logged in user")
                handleNotLoggedInUser()
            }
        }
    }

    private fun navigateToLoginScreen() {
        Timber.d("Navigating to login screen")
        if (firebaseAuth.currentUser?.isAnonymous == true) {
            Timber.d("Signing out anonymous user before navigation")
            firebaseAuth.signOut()
        }

        Intent(requireContext(), LoginActivity::class.java).also { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun handleFakeStoreUser() {
        binding.apply {
            Timber.d("Setting up UI for FakeStore user: $fakeStoreUsername")
            tvEmail.text = fakeStoreUsername
            tvName.text = fakeStoreUsername ?: getString(R.string.name)
            emailCard.visibility = View.GONE
            btnEditProfile.visibility = View.VISIBLE
            btnSettings.text = getString(R.string.settings)
            btnSettings.setOnClickListener { showSettingsOptions() }
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
            .setTitle(getString(R.string.guest_options)).setItems(options) { _, which ->
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
            }.show()
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
        android.app.AlertDialog.Builder(requireContext()).setTitle(getString(R.string.settings))
            .setItems(arrayOf("Logout")) { _, which ->
                when (which) {
                    0 -> logout()
                }
            }.show()
    }

    private fun logout() {
        if (isFakeStoreUser) {
            fakeStoreSessionManager.clearFakeStoreSession()
        } else {
            firebaseAuth.signOut()
        }
        navigateToLoginScreen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}