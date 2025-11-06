package com.example.myapplication.presenter.home

import com.example.myapplication.data.repository.FirebaseBookRepository
import com.example.myapplication.model.Book

class HomePresenter(private val view: HomeView) {
    private val repository = FirebaseBookRepository()
    private var allBooks: List<Book> = emptyList()

    fun loadBooks() {
        repository.getBooks(object : FirebaseBookRepository.BookCallback {
            override fun onSuccess(bookList: List<Book>) {
                allBooks = bookList
            }

            override fun onError(error: String) {
                view.showError("Error al cargar los libros: $error")
            }
        })
    }

    fun searchBooksByTitle(query: String) {
        val filtrados = if (query.isEmpty()) {
            allBooks
        } else {
            allBooks.filter { book ->
                book.titleBook.contains(query, ignoreCase = true)
            }
        }

        view.showBooks(filtrados)
    }

    fun onTopBooksClicked() = view.navigateToTopBooks()
}
