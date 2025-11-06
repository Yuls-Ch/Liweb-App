package com.example.myapplication.presenter.splash

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.myapplication.utils.SessionManager

class SplashPresenter(
    private val view: SplashView,
    private val context: Context
) {

    fun startSplash(delayMillis: Long = 3000L) {
        Handler(Looper.getMainLooper()).postDelayed({
            checkSession()
        }, delayMillis)
    }

    private fun checkSession() {
        val sessionManager = SessionManager(context)
        if (sessionManager.isLoggedIn()) {
            view.navigateToHome()
        } else {
            view.navigateToOptions()
        }
    }
}