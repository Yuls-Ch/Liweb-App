package com.example.myapplication.presenter.favorite

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.BookFavoriteAdapter
import com.example.myapplication.databinding.ActivityBookFavoriteBinding
import com.example.myapplication.model.Book
import com.example.myapplication.presenter.base.BaseActivity

class FavoriteBookActivity : BaseActivity(), FavoriteBookView {

    private lateinit var binding: ActivityBookFavoriteBinding
    private lateinit var favoritePresenter: FavoriteBookPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favoritePresenter = FavoriteBookPresenter(this, applicationContext)

        setupListeners()
        favoritePresenter.onViewCreated()
    }

    private fun setupListeners() {
        binding.myMaterialIconButton.setOnClickListener {
            favoritePresenter.onExitButtonClicked()
        }
        binding.btnVolver.setOnClickListener {
            favoritePresenter.onBackButtonClicked()
        }
        binding.myIconButton.setOnClickListener {
            favoritePresenter.onThemeButtonClicked()
        }
    }

    override fun displayFavoriteBooks(books: List<Book>) {
        binding.recyclerViewLibros.apply {
            layoutManager = GridLayoutManager(this@FavoriteBookActivity, 2)
            adapter = BookFavoriteAdapter(books)
            setHasFixedSize(true)
        }
    }

    override fun updateTheme(isDark: Boolean) {
        binding.myIconButton.setImageResource(
            if (isDark) R.drawable.moon_icon else R.drawable.sun_icon
        )
        binding.imageCurvaInferior.setImageResource(
            if (isDark) R.drawable.desing_dark else R.drawable.desing_lower
        )
    }

    override fun closeView() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        favoritePresenter.detachView()
    }
}