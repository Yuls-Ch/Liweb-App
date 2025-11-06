package com.example.myapplication.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemBookBinding
import com.example.myapplication.model.Book
import com.example.myapplication.presenter.detail.DetailBookActivity

class BookAdapter(private val bookList: List<Book>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    inner class BookViewHolder(val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        val context = holder.itemView.context

        holder.binding.root.visibility = View.INVISIBLE

        holder.binding.titleBook.text = book.titleBook
        holder.binding.priceBook.text = context.getString(R.string.book_price, book.priceBook)

        Glide.with(context)
            .load(book.imageBook)
            .transform(RoundedCorners(30))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.root.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.binding.root.visibility = View.VISIBLE
                    return false
                }
            })
            .into(holder.binding.imageBook)

        holder.binding.root.setOnClickListener {
            val intent = Intent(context, DetailBookActivity::class.java)
            intent.putExtra("book_detail", book)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = bookList.size
}
