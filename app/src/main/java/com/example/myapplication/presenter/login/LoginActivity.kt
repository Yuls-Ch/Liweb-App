package com.example.myapplication.presenter.login

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.presenter.home.HomeActivity
import com.example.myapplication.presenter.options.OptionsRegisterActivity
import com.example.myapplication.utils.ThemeManager
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Suppress("DEPRECATION")

class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var presenter: LoginPresenter
    private lateinit var oneTapClient: SignInClient
    private val auth = FirebaseAuth.getInstance()

    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) firebaseAuthWithGoogle(idToken)
                    else showToast("Token inválido de Google")
                } catch (e: Exception) {
                    showToast("Error: ${e.message}")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPresenter()
        setupThemeButton()
        setupListeners()
        setupGoogleSignIn()

        auth.currentUser?.let {
            navigateToHome()
        }
    }

    private fun initPresenter() {
        presenter = LoginPresenter(this)
    }

    private fun setupGoogleSignIn() {
        oneTapClient = Identity.getSignInClient(this)
    }

    private fun setupListeners() = with(binding) {
        btnIngresar.setOnClickListener {
            val email = edtCorreo.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            presenter.loginWithEmail(email, password)
        }

        btnGoogleSignIn.setOnClickListener {
            launchGoogleSignIn()
        }

        btnVolver.setOnClickListener {
            startActivity(Intent(this@LoginActivity, OptionsRegisterActivity::class.java))
        }
    }

    private fun launchGoogleSignIn() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                val request = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                googleLauncher.launch(request)
            }
            .addOnFailureListener {
                Log.e("GOOGLE_SIGN_IN", "Error detallado:", it)
                showToast("Error: ${it.message}")
            }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                val user = auth.currentUser
                showToast("Bienvenido ${user?.displayName ?: "usuario"}")
                navigateToHome()
            }
            .addOnFailureListener {
                showToast("Error: ${it.message}")
            }
    }

    override fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupThemeButton() {
        val colorBlueLight = ContextCompat.getColor(this, R.color.blue)
        val colorBlueStrong = ContextCompat.getColor(this, R.color.blue_strong)
        val colorBlueMediumDark = ContextCompat.getColor(this, R.color.blue_medium_dark)
        val colorWhite = ContextCompat.getColor(this, R.color.white)
        val colorBlack = ContextCompat.getColor(this, R.color.black)
        fun tint(color: Int) = ColorStateList.valueOf(color)

        with(binding) {
            if (ThemeManager.isDarkMode(this@LoginActivity)) {
                imgFondo.setImageResource(R.drawable.theme_black)
                loginBox.backgroundTintList = tint(colorBlueStrong)
                edtCorreo.backgroundTintList = tint(colorBlack)
                edtPassword.backgroundTintList = tint(colorBlack)
                edtCorreo.setTextColor(colorWhite)
                edtPassword.setTextColor(colorWhite)
                btnIngresar.backgroundTintList = tint(colorBlueLight)
            } else {
                imgFondo.setImageResource(R.drawable.fondo)
                loginBox.backgroundTintList = tint(colorBlueLight)
                edtCorreo.setTextColor(colorBlack)
                edtPassword.setTextColor(colorBlack)
                btnIngresar.backgroundTintList = tint(colorBlueMediumDark)
            }
        }
    }
}
