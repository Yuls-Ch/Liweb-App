package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.CartbookItemBinding
import com.example.myapplication.model.Book
import com.example.myapplication.presenter.shopping.manager.CartManager

class CartAdapter(
    private val cartList: MutableList<Book>,
    private val userId: String,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartbookItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartList[position], cartList, userId, onCartUpdated, this)
    }

    override fun getItemCount() = cartList.size

    class CartViewHolder(private val binding: CartbookItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            book: Book,
            cartList: MutableList<Book>,
            userId: String,
            onCartUpdated: () -> Unit,
            adapter: RecyclerView.Adapter<*>
        ) {
            binding.tvBookTitle.text = book.titleBook
            binding.tvPriceValue.text =
                binding.root.context.getString(R.string.book_price_format, book.priceBook)
            binding.tvQuantity.text = book.quantitySelected.toString()

            Glide.with(binding.root)
                .load(book.imageBook)
                .into(binding.ivBookImage)

            binding.btnDelete.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    CartManager.removeBook(binding.root.context, userId, book.titleBook)
                    cartList.removeAt(pos)
                    adapter.notifyItemRemoved(pos)
                    adapter.notifyItemRangeChanged(pos, cartList.size)
                    onCartUpdated()
                    Toast.makeText(binding.root.context, "Removed from cart", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
