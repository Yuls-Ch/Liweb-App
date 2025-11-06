package com.example.myapplication.presenter.catalogBooks

import com.example.myapplication.model.Book

interface CatalogBooksView {
    fun showBooks(bookList: List<Book>)
    fun showError(message: String)
    fun showEmptyResults(originalBooks: List<Book>)
    fun showNoResultsMessage(message: String)
}