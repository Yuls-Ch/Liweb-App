package com.example.myapplication.presenter.detail

import com.example.myapplication.model.Book
import com.example.myapplication.presenter.base.BaseView

interface DetailBookView : BaseView {
    fun displayBookDetails(book: Book)
    fun updateFavoriteButton(isFavorite: Boolean)
    fun updateQuantity(quantity: Int)
    fun updateTheme(isDark: Boolean)
    fun showToast(message: String)
    fun showInsufficientStockError(availableStock: Int)
    fun updateStockRealtime(newStock: Int)
    fun navigateToCatalog()
    fun navigateToShoppingCart()
    fun closeView()
}