    package com.upi.masakin.ui.view.auth

    import android.content.Intent
    import android.os.Bundle
    import android.view.View
    import android.widget.Toast
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.appcompat.app.AppCompatActivity
    import androidx.lifecycle.lifecycleScope
    import com.google.android.gms.auth.api.signin.GoogleSignIn
    import com.google.android.gms.auth.api.signin.GoogleSignInClient
    import com.google.android.gms.auth.api.signin.GoogleSignInOptions
    import com.google.android.gms.common.api.ApiException
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.GoogleAuthProvider
    import com.upi.masakin.R
    import com.upi.masakin.data.api.auth.FakeStoreApi
    import com.upi.masakin.data.api.auth.FakeStoreSessionManager
    import com.upi.masakin.data.api.auth.LoginRequest
    import com.upi.masakin.databinding.ActivityLoginBinding
    import com.upi.masakin.ui.view.main.MainActivity
    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.launch
    import timber.log.Timber
    import javax.inject.Inject

    @AndroidEntryPoint
    class LoginActivity : AppCompatActivity() {

        private lateinit var binding: ActivityLoginBinding
        private lateinit var googleSignInClient: GoogleSignInClient
        private var isFakeStoreUser: Boolean = false
        private var fakeStoreUsername: String? = null

        @Inject
        lateinit var firebaseAuth: FirebaseAuth

        @Inject
        lateinit var fakeStoreApi: FakeStoreApi

        @Inject
        lateinit var fakeStoreSessionManager: FakeStoreSessionManager

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
                    val email = binding.emailEditText.text.toString()
                    val password = binding.passwordEditText.text.toString()
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
            logoutFromGoogle() // Pastikan logout terlebih dahulu sebelum login ulang
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

        private fun logoutFromGoogle() {
            googleSignInClient.signOut()
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        showError("Failed to clear Google session")
                    }
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
            return true
        }


        private fun performLogin(emailOrUsername: String, password: String) {
            showLoading(true)

            // First try Firebase login if input matches email format
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrUsername).matches()) {
                firebaseAuth.signInWithEmailAndPassword(emailOrUsername, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navigateToMain()
                        } else {
                            // If Firebase login fails, try FakeStore API
                            loginWithFakeStoreApi(emailOrUsername, password)
                        }
                    }
                    .addOnFailureListener { e ->
                        showLoading(false)
                        showError("Firebase Login failed: ${e.localizedMessage}")
                    }
            } else {
                // If input is not an email, try FakeStore API directly
                loginWithFakeStoreApi(emailOrUsername, password)
            }
        }

        private fun loginWithFakeStoreApi(emailOrUsername: String, password: String) {
            lifecycleScope.launch {
                try {
                    showLoading(true)
                    val response = fakeStoreApi.login(LoginRequest(emailOrUsername, password))

                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val token = loginResponse?.token

                        if (!token.isNullOrEmpty()) {
                            fakeStoreSessionManager.saveFakeStoreSession(
                                username = emailOrUsername,
                                token = token
                            )

                            showLoading(false)
                            navigateToMain(emailOrUsername)
                            showError("Login successful with FakeStore API")
                        } else {
                            showLoading(false)
                            showError("Login failed: No token received")
                            Timber.d("Response Body: ${response.body()}")
                            Timber.e("Login failed: No token in response")
                        }
                    } else {
                        showLoading(false)
                        showError("Login failed: ${response.message()}")
                        Timber.e("Login failed: HTTP ${response.code()} - ${response.message()}")
                    }
                } catch (e: Exception) {
                    showLoading(false)
                    showError("Error: ${e.localizedMessage}")
                    Timber.e("Exception occurred: ${e.localizedMessage}", e)
                }
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

        // Di LoginActivity
        private fun navigateToMain(username: String? = null) {
            Intent(this, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                username?.let { name ->
                    it.putExtra("FAKESTORE_USERNAME", name)
                    it.putExtra("IS_FAKESTORE_USER", true)
                }
                startActivity(it)
                finish()
            }
        }
    }
