package com.example.myapplication.presenter.shopping.manager

import android.content.Context
import com.example.myapplication.model.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

object CartManager {
    private val userCarts = mutableMapOf<String, MutableList<Book>>()
    private const val PREF_NAME = "cart_prefs"

    private fun getUserCart(userId: String): MutableList<Book> {
        return userCarts.getOrPut(userId) { mutableListOf() }
    }

    fun loadCart(context: Context, userId: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(userId, null)

        if (json != null) {
            val type = object : TypeToken<MutableList<Book>>() {}.type
            val savedCart: MutableList<Book> = Gson().fromJson(json, type)
            userCarts[userId] = savedCart
        }
    }

    private fun saveCart(context: Context, userId: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            val json = Gson().toJson(userCarts[userId])
            putString(userId, json)
        }
    }

    fun addBook(context: Context, userId: String, book: Book, quantity: Int) {
        val userCart = getUserCart(userId)
        val existingBook = userCart.find { it.titleBook == book.titleBook }

        if (existingBook != null) {
            val totalQuantity = existingBook.quantitySelected + quantity
            existingBook.quantitySelected =
                if (totalQuantity > book.stock) book.stock else totalQuantity
        } else {
            val finalQuantity = if (quantity > book.stock) book.stock else quantity
            userCart.add(book.copy(quantitySelected = finalQuantity))
        }

        saveCart(context, userId)
    }

    fun getCart(userId: String): List<Book> = getUserCart(userId)

    fun getCartTotals(userId: String): Pair<Double, Int> {
        val cart = getUserCart(userId)
        val subtotal = cart.sumOf { it.priceBook * it.quantitySelected }
        val totalItems = cart.sumOf { it.quantitySelected }
        return Pair(subtotal, totalItems)
    }

    fun removeBook(context: Context, userId: String, title: String) {
        val userCart = getUserCart(userId)
        userCart.removeIf { it.titleBook == title }
        saveCart(context, userId)
    }

    fun clearCart(context: Context, userId: String) {
        userCarts[userId]?.clear()
        saveCart(context, userId)
    }

    fun clearAll(context: Context) {
        userCarts.clear()
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit { clear() }
    }
}
