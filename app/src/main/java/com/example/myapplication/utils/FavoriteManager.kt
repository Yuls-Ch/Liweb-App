package com.example.myapplication.utils

import android.content.Context
import com.example.myapplication.model.Book
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FavoriteManager {

    private const val PREF_NAME_BASE = "favorite_prefs"
    private const val KEY_FAVORITES = "favorite_books"

    private val favoriteBooks = mutableListOf<Book>()

    private fun getPrefsName(userEmail: String): String {
        return "${PREF_NAME_BASE}_$userEmail"
    }

    fun loadFavorites(context: Context, userEmail: String) {
        val prefs = context.getSharedPreferences(getPrefsName(userEmail), Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_FAVORITES, null)

        favoriteBooks.clear()
        if (json != null) {
            val type = object : TypeToken<MutableList<Book>>() {}.type
            val savedList: MutableList<Book> = Gson().fromJson(json, type)
            favoriteBooks.addAll(savedList)
        }
    }

    private fun saveFavorites(context: Context, userEmail: String) {
        val prefs = context.getSharedPreferences(getPrefsName(userEmail), Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(favoriteBooks)
        editor.putString(KEY_FAVORITES, json)
        editor.apply()
    }

    fun addFavorite(context: Context, userEmail: String, book: Book) {
        if (!favoriteBooks.any { it.titleBook == book.titleBook }) {
            favoriteBooks.add(book)
            saveFavorites(context, userEmail)
        }
    }

    fun removeFavorite(context: Context, userEmail: String, book: Book) {
        favoriteBooks.removeAll { it.titleBook == book.titleBook }
        saveFavorites(context, userEmail)
    }

    fun isFavorite(book: Book): Boolean {
        return favoriteBooks.any { it.titleBook == book.titleBook }
    }

    fun getFavorites(): List<Book> = favoriteBooks
}