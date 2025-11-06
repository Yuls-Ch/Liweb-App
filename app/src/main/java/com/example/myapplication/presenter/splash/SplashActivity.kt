package com.example.myapplication.presenter.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivitySplashBinding
import com.example.myapplication.presenter.home.HomeActivity
import com.example.myapplication.presenter.options.OptionsRegisterActivity

class SplashActivity : AppCompatActivity(), SplashView {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        presenter = SplashPresenter(this, this)
        presenter.startSplash()
    }

    override fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun navigateToOptions() {
        startActivity(Intent(this, OptionsRegisterActivity::class.java))
        finish()
    }
}