package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUserSession(email: String, name: String,password: String) {
        val editor = prefs.edit()
        editor.putString("USER_EMAIL", email)
        editor.putString("USER_NAME", name)
        editor.putString("USER_PASSWORD", password)
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.apply()
    }

    fun getUserName(): String? {
        return prefs.getString("USER_NAME", null)
    }
    fun getUserEmail(): String? {
        return prefs.getString("USER_EMAIL", null)
    }
    fun getUserPassword(): String?{
        return prefs.getString("USER_PASSWORD", null)
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("IS_LOGGED_IN", false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}