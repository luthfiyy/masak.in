package com.upi.masakin.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.upi.masakin.R
import com.upi.masakin.databinding.ActivityLoginBinding
import com.upi.masakin.ui.view.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { token ->
                    firebaseAuthWithGoogle(token)
                } ?: run {
                    showLoading(false)
                    showError("Google Sign In failed: No ID token found")
                }
            } catch (e: ApiException) {
                showLoading(false)
                showError("Google Sign In failed: ${e.statusCode}")
            }
        } catch (e: Exception) {
            showLoading(false)
            showError("Google Sign In failed: ${e.localizedMessage}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (firebaseAuth.currentUser != null) {
            navigateToMain()
            return
        }

        setupGoogleSignIn()
        setupClickListeners()
    }

    private fun setupGoogleSignIn() {
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)
        } catch (e: Exception) {
            showError("Failed to setup Google Sign In: ${e.localizedMessage}")
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            loginButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (validateInput(email, password)) {
                    performLogin(email, password)
                }
            }

            tvSignup.setOnClickListener {
                Intent(this@LoginActivity, RegisterActivity::class.java).also {
                    startActivity(it)
                }
            }

            googleLogin.setOnClickListener {
                try {
                    showLoading(true)
                    signInWithGoogle()
                } catch (e: Exception) {
                    showLoading(false)
                    showError("Failed to start Google Sign In: ${e.localizedMessage}")
                }
            }

            anonymousLogin.setOnClickListener {
                signInAnonymously()
            }

            btnBack.setOnClickListener { finish() }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    navigateToMain()
                } else {
                    showError("Authentication failed: ${task.exception?.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                showLoading(false)
                showError("Authentication failed: ${e.localizedMessage}")
            }
    }

    private fun signInAnonymously() {
        showLoading(true)
        firebaseAuth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    navigateToMain()
                    showError("Logged in as Guest")
                } else {
                    showError("Anonymous login failed: ${task.exception?.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                showLoading(false)
                showError("Anonymous login failed: ${e.localizedMessage}")
            }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill all fields")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Please enter a valid email")
            return false
        }
        return true
    }

    private fun performLogin(email: String, password: String) {
        showLoading(true)
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    navigateToMain()
                } else {
                    showError("Login failed: ${task.exception?.localizedMessage}")
                }
            }
            .addOnFailureListener { e ->
                showLoading(false)
                showError("Login failed: ${e.localizedMessage}")
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            loginButton.isEnabled = !isLoading
            googleLogin.isEnabled = !isLoading
            anonymousLogin.isEnabled = !isLoading
            emailEditText.isEnabled = !isLoading
            passwordEditText.isEnabled = !isLoading
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        Intent(this, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
            finish()
        }
    }
}