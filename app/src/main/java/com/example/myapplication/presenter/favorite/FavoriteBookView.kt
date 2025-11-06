package com.example.myapplication.presenter.favorite

import com.example.myapplication.model.Book
import com.example.myapplication.presenter.base.BaseView

interface FavoriteBookView : BaseView {
    fun displayFavoriteBooks(books: List<Book>)
    fun updateTheme(isDark: Boolean)
    fun closeView()
}