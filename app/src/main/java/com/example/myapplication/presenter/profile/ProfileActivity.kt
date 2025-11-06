package com.example.myapplication.presenter.profile

import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityProfileBinding
import com.example.myapplication.presenter.base.BaseActivity
import com.example.myapplication.utils.ThemeManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileActivity : BaseActivity(), ProfileView {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var presenter: ProfilePresenter
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = ProfilePresenter(this)
        presenter.loadUserData()

        setupReturnLoginButton(binding.myMaterialIconButton)
        setupThemeButton()

        binding.btnReturnHome.setOnClickListener {
            presenter.onReturnHomeClicked()
        }

        binding.btnUpdateProfile.setOnClickListener {
            updateUserName()
        }
    }

    override fun showUserData(nombre: String, correo: String, clave: String) {
        binding.edtNombre.setText(nombre)
        binding.edtCorreo.setText(correo)
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupThemeButton() {
        fun applyTheme(isDark: Boolean) {
            binding.myIconButton.setImageResource(
                if (isDark) R.drawable.moon_icon else R.drawable.sun_icon
            )
            binding.imageCurvaInferior.setImageResource(
                if (isDark) R.drawable.desing_dark else R.drawable.desing_lower
            )

            val mainColor = getColor(if (isDark) R.color.blue_strong else R.color.blue)
            val topBarColor = getColor(if (isDark) R.color.blue_light_dark else R.color.blue_light)

            with(binding) {
                myMaterialIconButton.setBackgroundColor(mainColor)
                btnReturnHome.setBackgroundColor(mainColor)
                btnUpdateProfile.setBackgroundColor(mainColor)
                cardViewProfile.setCardBackgroundColor(mainColor)
                topBarLayout.setBackgroundColor(topBarColor)
            }
        }

        val isDark = ThemeManager.isDarkMode(this)
        applyTheme(isDark)

        binding.myIconButton.setOnClickListener {
            val newTheme = ThemeManager.toggleTheme(this)
            applyTheme(newTheme)
        }
    }

    private fun updateUserName() {
        val user = auth.currentUser
        val newName = binding.edtNombre.text.toString().trim()

        if (user == null) {
            showMessage("No hay usuario autenticado")
            return
        }

        val currentName = user.displayName ?: ""

        if (newName.isEmpty()) {
            showMessage("Ingresa un nombre válido")
            return
        }

        if (newName == currentName) {
            showMessage("No se detectaron cambios en el nombre")
            return
        }

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                showMessage("Nombre actualizado correctamente")
            }
            .addOnFailureListener {
                showMessage("Error al actualizar el nombre: ${it.message}")
            }
    }

}
