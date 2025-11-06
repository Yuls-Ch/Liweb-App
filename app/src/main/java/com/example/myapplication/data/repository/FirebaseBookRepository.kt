package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.model.Book
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseBookRepository {
    private val databaseRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("books")

    interface BookCallback {
        fun onSuccess(bookList: List<Book>)
        fun onError(error: String)
    }

    fun getBooks(callback: BookCallback) {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<Book>()
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null) books.add(book)
                }
                callback.onSuccess(books)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error al leer libros: ${error.message}")
                callback.onError(error.message)
            }
        })
    }
}