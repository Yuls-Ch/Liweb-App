package com.example.myapplication.presenter.catalogBooks

import com.example.myapplication.data.repository.FirebaseBookRepository
import com.example.myapplication.model.Book

class CatalogBooksPresenter(private val view: CatalogBooksView) {
    private val repository = FirebaseBookRepository()
    private var allBooks: List<Book> = emptyList()
    private var dialogFilteredBooks: List<Book>? = null

    fun loadBooks() {
        repository.getBooks(object : FirebaseBookRepository.BookCallback {
            override fun onSuccess(bookList: List<Book>) {
                allBooks = bookList
                view.showBooks(bookList)
            }

            override fun onError(error: String) {
                view.showError("Error al cargar los libros: $error")
            }
        })
    }

    fun filterBooks(category: String, maxPrice: Double) {
        val filtered = allBooks.filter { book ->
            (category.isEmpty() || book.nameCategory.equals(category, ignoreCase = true)) &&
                    (book.priceBook <= maxPrice)
        }
        dialogFilteredBooks = filtered

        if (filtered.isEmpty()) {
            view.showEmptyResults(allBooks)
        } else {
            view.showBooks(filtered)
        }
    }

    fun searchBooksByTitle(query: String) {
        val sourceList = dialogFilteredBooks?.takeIf { it.isNotEmpty() } ?: allBooks

        val filtrados = if (query.isEmpty()) {
            sourceList
        } else {
            sourceList.filter { book ->
                book.titleBook.contains(query, ignoreCase = true)
            }
        }

        if (filtrados.isEmpty()) {
            view.showNoResultsMessage("No se encuentran resultados")
        } else {
            view.showBooks(filtrados)
        }
    }

    fun clearFilters() {
        dialogFilteredBooks = null
    }
}