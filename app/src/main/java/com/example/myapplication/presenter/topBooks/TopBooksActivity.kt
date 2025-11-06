package com.example.myapplication.presenter.topBooks

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.TopBooksAdapter
import com.example.myapplication.databinding.ActivityTopBooksBinding
import com.example.myapplication.model.Book
import com.example.myapplication.presenter.base.BaseActivity
import com.example.myapplication.presenter.home.HomeActivity
import com.example.myapplication.utils.ThemeManager

class TopBooksActivity : BaseActivity(), TopBooksView {

    private lateinit var presenter: TopBooksPresenter
    private lateinit var binding: ActivityTopBooksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyTheme(this)
        binding = ActivityTopBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = TopBooksPresenter(this)

        setupRecycler()
        setupBackButton()
        setupThemeButton()

        setupReturnLoginButton(binding.btnClose)

        presenter.loadTopBooks()
    }


    private fun setupRecycler() {
        binding.recyclerListTopBooks.layoutManager = LinearLayoutManager(this)
    }

    private fun setupBackButton() {
        binding.btnVolver.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    override fun showTopBooks(bookList: List<Book>) {
        binding.recyclerListTopBooks.adapter = TopBooksAdapter(bookList)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupThemeButton() {
        if (ThemeManager.isDarkMode(this)) {
            binding.icSun.setImageResource(R.drawable.moon_icon)
            binding.imageCurvaInferior.setImageResource(R.drawable.desing_dark)
        } else {
            binding.icSun.setImageResource(R.drawable.sun_icon)
            binding.imageCurvaInferior.setImageResource(R.drawable.desing_lower)
        }

        binding.icSun.setOnClickListener {
            val isNowDark = ThemeManager.toggleTheme(this)
            binding.icSun.setImageResource(if (isNowDark) R.drawable.moon_icon else R.drawable.sun_icon)
            binding.imageCurvaInferior.setImageResource(if (isNowDark) R.drawable.desing_dark else R.drawable.desing_lower)
        }
    }

}
