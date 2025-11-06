package com.example.myapplication.presenter.shopping

import com.example.myapplication.model.Book

interface ShoppingCartView {
    fun showCartItem(cartList: List<Book>)
    fun showTotals(subtotal: Double, total: Double)
    fun showEmptyCartMessage()
    fun iniciarPago(total: Double)
}