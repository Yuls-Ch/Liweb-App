package com.example.myapplication.presenter.register

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.example.myapplication.presenter.login.LoginActivity
import com.example.myapplication.utils.ThemeManager
@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity(), RegisterView {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var presenter: RegisterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = RegisterPresenter(this)

        setupBackButton()
        setupRegisterButton()
        setupThemeButton()
    }

    private fun setupRegisterButton() {
        binding.btnRegistrar.setOnClickListener {
            val name = binding.edtNombre.text.toString().trim()
            val email = binding.edtCorreo.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            presenter.registerUser(name, email, password)
            binding.btnRegistrar.postDelayed({ binding.btnRegistrar.isEnabled = true }, 3000)
        }
    }

    private fun setupBackButton() {
        binding.btnVolver.setOnClickListener { finish() }
    }

    private fun setupThemeButton() {
        val colorBlueLight = ContextCompat.getColor(this, R.color.blue)
        val colorBlueStrong = ContextCompat.getColor(this, R.color.blue_strong)
        val colorBlueMediumDark = ContextCompat.getColor(this, R.color.blue_medium_dark)
        val colorDarkGray = ContextCompat.getColor(this, R.color.dark_gray_bg)
        val colorWhite = ContextCompat.getColor(this, R.color.white)
        val colorBlack = ContextCompat.getColor(this, R.color.black)

        fun tint(color: Int) = ColorStateList.valueOf(color)

        with(binding) {
            if (ThemeManager.isDarkMode(this@RegisterActivity)) {
                imgFondo.setImageResource(R.drawable.theme_black)
                loginBox.backgroundTintList = tint(colorBlueStrong)
                edtNombre.backgroundTintList = tint(colorDarkGray)
                edtCorreo.backgroundTintList = tint(colorDarkGray)
                edtPassword.backgroundTintList = tint(colorDarkGray)
                btnRegistrar.backgroundTintList = tint(colorBlueLight)

                edtNombre.setTextColor(colorWhite)
                edtCorreo.setTextColor(colorWhite)
                edtPassword.setTextColor(colorWhite)
            } else {
                imgFondo.setImageResource(R.drawable.fondo)
                loginBox.backgroundTintList = tint(colorBlueLight)
                edtNombre.backgroundTintList = tint(colorWhite)
                edtCorreo.backgroundTintList = tint(colorWhite)
                edtPassword.backgroundTintList = tint(colorWhite)
                btnRegistrar.backgroundTintList = tint(colorBlueMediumDark)

                edtNombre.setTextColor(colorBlack)
                edtCorreo.setTextColor(colorBlack)
                edtPassword.setTextColor(colorBlack)
            }
        }
    }

    override fun showNameError(message: String) {
        binding.layoutNombre.error = message
    }

    override fun showEmailError(message: String) {
        binding.layoutCorreo.error = message
    }

    override fun showPasswordError(message: String) {
        binding.layoutPassword.error = message
    }

    override fun clearErrors() {
        with(binding) {
            layoutNombre.error = null
            layoutCorreo.error = null
            layoutPassword.error = null
        }
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
