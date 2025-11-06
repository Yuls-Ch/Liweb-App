package com.example.myapplication.presenter.detail

import android.content.Context
import com.example.myapplication.R
import com.example.myapplication.model.Book
import com.example.myapplication.presenter.base.BasePresenter
import com.example.myapplication.presenter.shopping.manager.CartManager
import com.example.myapplication.utils.FavoriteManager
import com.example.myapplication.utils.ThemeManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailBookPresenter(
    private var detailView: DetailBookView?,
    private val context: Context,
    private val book: Book
) : BasePresenter(detailView!!) {

    private var currentQuantity = 1
    private var maxStock = book.stock
    private val userEmail: String
        get() = FirebaseAuth.getInstance().currentUser?.email ?: "guest"

    fun onViewCreated() {
        FavoriteManager.loadFavorites(context, userEmail)
        detailView?.displayBookDetails(book)
        detailView?.updateFavoriteButton(FavoriteManager.isFavorite(book))
        detailView?.updateQuantity(currentQuantity)
        detailView?.updateTheme(ThemeManager.isDarkMode(context))
    }

    fun onToggleFavorite() {
        val isNowFavorite = !FavoriteManager.isFavorite(book)
        if (isNowFavorite) {
            FavoriteManager.addFavorite(context, userEmail, book)
            detailView?.showToast("Se agregó a favoritos")
        } else {
            FavoriteManager.removeFavorite(context, userEmail, book)
            detailView?.showToast("Se quitó de favoritos")
        }
        detailView?.updateFavoriteButton(isNowFavorite)
    }

    fun onIncreaseQuantity() {
        if (currentQuantity < maxStock) {
            currentQuantity++
            detailView?.updateQuantity(currentQuantity)
        }
    }

    fun onDecreaseQuantity() {
        if (currentQuantity > 1) {
            currentQuantity--
            detailView?.updateQuantity(currentQuantity)
        }
    }

    fun onAddToCartClicked() {
        val quantity = currentQuantity
        CartManager.addBook(context, userEmail, book, quantity)
        CartManager.loadCart(context, userEmail)
        detailView?.showToast("Libro añadido al carrito")
    }

    fun onCartIconClicked() {
        CartManager.loadCart(context, userEmail)
        val cartItems = CartManager.getCart(userEmail)
        if (cartItems.isEmpty()) {
            detailView?.showEmptyCartDialog()
        } else {
            detailView?.navigateToShoppingCart()
        }
    }

    fun observarStockTiempoReal() {
        val ref = FirebaseDatabase.getInstance().getReference("books")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val titleInDb = child.child("titleBook").getValue(String::class.java)
                    if (titleInDb.equals(book.titleBook, ignoreCase = true)) {
                        val nuevoStock = child.child("stock").getValue(Int::class.java) ?: 0
                        maxStock = nuevoStock
                        detailView?.updateStockRealtime(nuevoStock)
                        break
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun obtenerStockActual() {
        val ref = FirebaseDatabase.getInstance().getReference("books")
        ref.get().addOnSuccessListener { snapshot ->
            for (child in snapshot.children) {
                val titleInDb = child.child("titleBook").getValue(String::class.java)
                if (titleInDb.equals(book.titleBook, ignoreCase = true)) {
                    val stockActual = child.child("stock").getValue(Int::class.java) ?: 0
                    detailView?.updateStockRealtime(stockActual)
                    break
                }
            }
        }
    }

    fun onThemeToggled() {
        val isNowDark = ThemeManager.toggleTheme(context)
        detailView?.updateTheme(isNowDark)
    }

    fun onBackClicked() {
        detailView?.navigateToCatalog()
    }

    fun onDestroy() {
        detailView = null
    }
}