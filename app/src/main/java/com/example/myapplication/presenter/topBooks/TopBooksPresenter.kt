package com.example.myapplication.presenter.topBooks

import com.example.myapplication.model.Book
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TopBooksPresenter(private val view: TopBooksView) {

    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("books")

    fun loadTopBooks() {
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allBooks = mutableListOf<Book>()

                for (bookSnap in snapshot.children) {
                    val book = bookSnap.getValue(Book::class.java)
                    if (book != null) {
                        allBooks.add(book)
                    }
                }

                if (allBooks.isNotEmpty()) {
                    val randomTopFive = allBooks.shuffled().take(5)
                    view.showTopBooks(randomTopFive)
                } else {
                    view.showError("No books available")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                view.showError("Error loading books: ${error.message}")
            }
        })
    }
}