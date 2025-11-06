package com.example.myapplication.presenter.home

import com.example.myapplication.model.Book

interface HomeView {
    fun showWelcomeMessage(message: String)
    fun navigateToProfile(nombre: String?, correo: String?, clave: String?)
    fun navigateToCatalog()
    fun navigateToTopBooks()
    fun navigateToFavorites()
    fun navigateToCart(isEmpty: Boolean)
    fun showBooks(bookList: List<Book>)
    fun showError(message: String)
}