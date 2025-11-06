package com.example.myapplication.presenter.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityDetailBookBinding
import com.example.myapplication.model.Book
import com.example.myapplication.presenter.base.BaseActivity
import com.example.myapplication.presenter.catalogBooks.CatalogBooksActivity
import com.example.myapplication.presenter.shopping.ShoppingCartActivity

class DetailBookActivity : BaseActivity(), DetailBookView {

    private lateinit var binding: ActivityDetailBookBinding
    private lateinit var detailPresenter: DetailBookPresenter

    private val shoppingCartLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {

                detailPresenter.obtenerStockActual()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val book = intent.getParcelableExtra<Book>("book_detail")
        if (book == null) {
            closeView()
            return
        }

        detailPresenter = DetailBookPresenter(this, applicationContext, book)

        setupListeners()
        detailPresenter.onViewCreated()
        detailPresenter.observarStockTiempoReal()

    }

    private fun setupListeners() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnFavorite.setOnClickListener { detailPresenter.onToggleFavorite() }
        binding.btnIncrease.setOnClickListener { detailPresenter.onIncreaseQuantity() }
        binding.btnDecrease.setOnClickListener { detailPresenter.onDecreaseQuantity() }
        binding.btnCart.setOnClickListener { detailPresenter.onAddToCartClicked() }
        binding.myIconButton.setOnClickListener { detailPresenter.onThemeToggled() }
        binding.btnBack.setOnClickListener { detailPresenter.onBackClicked() }
        binding.myMaterialIconButton.setOnClickListener { detailPresenter.onExitButtonClicked() }
        binding.imageShopBook.setOnClickListener { detailPresenter.onCartIconClicked() }
    }

    override fun displayBookDetails(book: Book) {
        Glide.with(this).load(book.imageBook).centerCrop().into(binding.bannerLibro)
        binding.tituloLibro.text = book.titleBook
        binding.txtNombreAutorValor.text = "Desconocido"
        binding.txtSinopsisTexto.text = book.synopsisBook
        binding.txtCategoriaValor.text = book.nameCategory
        binding.txtAnioPublicacionValor.text = "No disponible"
        binding.txtPrecioValor.text = getString(R.string.book_price, book.priceBook)
    }

    override fun updateFavoriteButton(isFavorite: Boolean) {
        val colorRes = if (isFavorite) R.color.blue else R.color.gray_900
        binding.btnFavorite.backgroundTintList = ContextCompat.getColorStateList(this, colorRes)
    }

    override fun updateQuantity(quantity: Int) {
        binding.txtCantidad.text = quantity.toString()
    }

    override fun updateTheme(isDark: Boolean) {
        binding.myIconButton.setImageResource(if (isDark) R.drawable.moon_icon else R.drawable.sun_icon)
        binding.imageView2.setImageResource(if (isDark) R.drawable.desing_dark else R.drawable.desing_lower)
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showInsufficientStockError(availableStock: Int) {
        showToast(getString(R.string.stock_insuficent, availableStock))
    }

    @SuppressLint("SetTextI18n")
    override fun updateStockRealtime(newStock: Int) {
        runOnUiThread {
            binding.txtPrecioValor.text = "Stock disponible: $newStock"
        }
    }

    override fun navigateToCatalog() {
        startActivity(Intent(this, CatalogBooksActivity::class.java))
        closeView()
    }

    override fun navigateToShoppingCart() {
        val intent = Intent(this, ShoppingCartActivity::class.java)
        shoppingCartLauncher.launch(intent)
    }


    override fun closeView() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        detailPresenter.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        detailPresenter.obtenerStockActual()
        detailPresenter.observarStockTiempoReal()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            detailPresenter.obtenerStockActual()
        }
    }


}