package com.example.myapplication.presenter.shopping

import android.content.Context
import com.example.myapplication.presenter.shopping.manager.CartManager
import com.google.firebase.database.FirebaseDatabase

class ShoppingCartPresenter(
    private val context: Context,
    private val view: ShoppingCartView
) {

    private val database = FirebaseDatabase.getInstance().getReference("books")

    fun loadCart(userId: String) {
        val cartList = CartManager.getCart(userId)
        if (cartList.isEmpty()) {
            view.showEmptyCartMessage()
        } else {
            view.showCartItem(cartList)
            updateTotals(userId)
        }
    }

    fun updateTotals(userId: String) {
        val (subtotal, _) = CartManager.getCartTotals(userId)
        val total = subtotal
        view.showTotals(subtotal, total)
    }

    fun onBuyButtonClicked(total: Double) {
        view.iniciarPago(total)
    }

    fun clearCart(userId: String, mostrarDialog: Boolean = true) {
        CartManager.clearCart(context, userId)
        if (mostrarDialog) {
            view.showEmptyCartMessage()
        }
    }

    fun descontarStockFirebase(userId: String) {
        val cartList = CartManager.getCart(userId)
        if (cartList.isEmpty()) return

        for (book in cartList) {
            val title = book.titleBook.trim()
            val cantidadComprada = book.quantitySelected

            database.get().addOnSuccessListener { snapshot ->
                for (child in snapshot.children) {
                    val titleInDb = child.child("titleBook").getValue(String::class.java)
                    if (titleInDb.equals(title, ignoreCase = true)) {
                        val stockActual = child.child("stock").getValue(Int::class.java) ?: 0
                        val nuevoStock = (stockActual - cantidadComprada).coerceAtLeast(0)

                        child.ref.child("stock").setValue(nuevoStock)
                            .addOnSuccessListener {
                                println("Stock actualizado para '$title': $nuevoStock unidades restantes.")
                            }
                            .addOnFailureListener {
                                println("Error al actualizar stock de '$title': ${it.message}")
                            }
                        break
                    }
                }
            }.addOnFailureListener {
                println("Error al leer libros: ${it.message}")
            }
        }
    }
}
