package com.example.myapplication.presenter.options

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityOptionsRegisterBinding
import com.example.myapplication.presenter.login.LoginActivity
import com.example.myapplication.presenter.register.RegisterActivity
import com.example.myapplication.utils.ThemeManager

class OptionsRegisterActivity : AppCompatActivity(), OptionsRegisterView {

    private lateinit var binding: ActivityOptionsRegisterBinding
    private lateinit var presenter: OptionsRegisterPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOptionsRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        presenter = OptionsRegisterPresenter(this)
        setupButtons()
        setupThemeButton()
    }

    private fun setupButtons() = with(binding) {
        btnUno.setOnClickListener { presenter.onLoginClicked() }
        btnDos.setOnClickListener { presenter.onRegisterClicked() }
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    private fun setupThemeButton() {
        val colorClaro = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue))
        val colorOscuro = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.blue_strong))

        with(binding) {
            if (ThemeManager.isDarkMode(this@OptionsRegisterActivity)) {
                fondo.setImageResource(R.drawable.theme_black)
                btnUno.backgroundTintList = colorOscuro
                btnDos.backgroundTintList = colorOscuro
            } else {
                fondo.setImageResource(R.drawable.fondo)
                btnUno.backgroundTintList = colorClaro
                btnDos.backgroundTintList = colorClaro
            }
        }
    }
}
