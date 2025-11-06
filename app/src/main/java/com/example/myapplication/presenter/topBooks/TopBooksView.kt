package com.example.myapplication.presenter.topBooks

import com.example.myapplication.model.Book

interface TopBooksView {
    fun showTopBooks(bookList: List<Book>)
    fun showError(message: String)
}