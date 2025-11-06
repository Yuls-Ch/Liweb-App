package com.example.myapplication.presenter.favorite

import android.content.Context
import com.example.myapplication.presenter.base.BasePresenter
import com.example.myapplication.utils.FavoriteManager
import com.example.myapplication.utils.ThemeManager
import com.google.firebase.auth.FirebaseAuth

class FavoriteBookPresenter(
    private var favoriteView: FavoriteBookView?,
    private val context: Context
) : BasePresenter(favoriteView!!) {

    private val userEmail: String
        get() = FirebaseAuth.getInstance().currentUser?.email ?: "guest"

    fun onViewCreated() {
        FavoriteManager.loadFavorites(context, userEmail)
        val books = FavoriteManager.getFavorites()
        favoriteView?.displayFavoriteBooks(books)
        favoriteView?.updateTheme(ThemeManager.isDarkMode(context))
    }

    fun onThemeButtonClicked() {
        val isNowDark = ThemeManager.toggleTheme(context)
        favoriteView?.updateTheme(isNowDark)
    }

    fun onBackButtonClicked() {
        favoriteView?.closeView()
    }

    fun detachView() {
        favoriteView = null
    }
}