package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.myapplication.databinding.ItemTopBookBinding
import com.example.myapplication.model.Book
import com.example.myapplication.R

class TopBooksAdapter(private val cartBookList: List<Book>) :
    RecyclerView.Adapter<TopBooksAdapter.BookViewHolder>() {

    inner class BookViewHolder(private val binding: ItemTopBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.titleBook.text = book.titleBook
            binding.synopsisBook.text = book.synopsisBook

            val correctPosition = bindingAdapterPosition + 1
            binding.numberPosition.text = binding.root.context.getString(R.string.position_format, correctPosition)

            binding.nameCategory.text = book.nameCategory

            Glide.with(binding.root.context)
                .load(book.imageBook)
                .transform(RoundedCorners(30))
                .into(binding.imageBook)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemTopBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(cartBookList[position])
    }

    override fun getItemCount(): Int = cartBookList.size
}
