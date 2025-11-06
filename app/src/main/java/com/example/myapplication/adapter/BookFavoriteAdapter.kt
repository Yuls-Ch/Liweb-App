package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemFavoriteBinding
import com.example.myapplication.model.Book

class BookFavoriteAdapter(private val bookList: List<Book>) :
    RecyclerView.Adapter<BookFavoriteAdapter.BookFavoriteViewHolder>() {

    inner class BookFavoriteViewHolder(val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookFavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookFavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookFavoriteViewHolder, position: Int) {
        val book = bookList[position]

        holder.binding.titleBook.text = book.titleBook

        Glide.with(holder.itemView.context)
            .load(book.imageBook)
            .centerCrop()
            .into(holder.binding.imageBook)
    }

    override fun getItemCount(): Int = bookList.size
}