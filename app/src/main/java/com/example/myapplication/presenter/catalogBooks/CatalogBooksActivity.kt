package com.example.myapplication.presenter.catalogBooks

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.TypedValue
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.text.TextWatcher
import android.widget.Toast
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.BookAdapter
import com.example.myapplication.databinding.ActivityCatalogBooksBinding
import com.example.myapplication.databinding.ModalFilterBinding
import com.example.myapplication.model.Book
import com.example.myapplication.presenter.base.BaseActivity
import com.example.myapplication.presenter.shopping.ShoppingCartActivity
import com.example.myapplication.presenter.shopping.manager.CartManager
import com.example.myapplication.utils.ThemeManager
import com.google.android.material.bottomsheet.BottomSheetDialog

class CatalogBooksActivity : BaseActivity(), CatalogBooksView {
    private lateinit var binding: ActivityCatalogBooksBinding
    private lateinit var presenter: CatalogBooksPresenter
    private lateinit var booksAdapter: BookAdapter
    private lateinit var filteredBooks: MutableList<Book>
    private var filteredByDialogBooks: MutableList<Book>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)

        binding = ActivityCatalogBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = CatalogBooksPresenter(this)

        setupUI()
        presenter.loadBooks()
        setupFilterButton()
        setupSearchBar()
        setupThemeButton()
        setupCartButton()
    }

    private fun setupUI() {
        setupReturnLoginButton(binding.myMaterialIconButton)
        setupBooksRecyclerView()
    }

    private fun setupBooksRecyclerView() {
        val recyclerView = binding.recyclerListBooks
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        filteredBooks = mutableListOf()
        booksAdapter = BookAdapter(filteredBooks)
        recyclerView.adapter = booksAdapter
    }

    fun setupFilterButton() {
        binding.btnFilter.setOnClickListener {
            displayFilterBottomSheet()
        }
    }

    private fun displayFilterBottomSheet() {
        val dialog = BottomSheetDialog(this)
        val bindingModal = ModalFilterBinding.inflate(layoutInflater)
        dialog.setContentView(bindingModal.root)

        val categoryInput = bindingModal.etCategory
        val dropdownIcon = bindingModal.imgDropdown
        val categoryList = bindingModal.llCategoryList
        val priceInput = bindingModal.etPrecio
        val applyButton = bindingModal.iconButtonApply
        val clearButton = bindingModal.iconButtonClear

        val categories = listOf("FANTASIA", "ROMANCE", "TECNOLOGIA", "HISTORIA", "TERROR")

        initializeCategoryDropdown(categoryInput, dropdownIcon, categoryList, categories)

        applyButton.setOnClickListener {
            val category = categoryInput.text.toString().trim()
            val price = priceInput.text.toString().toDoubleOrNull() ?: Double.MAX_VALUE
            presenter.filterBooks(category, price)
            dialog.dismiss()
        }

        clearButton.setOnClickListener {
            categoryInput.text?.clear()
            priceInput.text?.clear()
            binding.myEditText.text.clear()

            presenter.clearFilters()
            presenter.loadBooks()

            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initializeCategoryDropdown(
        categoryInput: EditText,
        icon: ImageView,
        listContainer: LinearLayout,
        categories: List<String>
    ) {
        fun getThemeColor(context: Context, attr: Int): Int {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(attr, typedValue, true)
            return if (typedValue.resourceId != 0)
                ContextCompat.getColor(context, typedValue.resourceId)
            else
                typedValue.data
        }

        val backgroundColor = getThemeColor(categoryInput.context, com.google.android.material.R.attr.containerColor)
        val textColor = getThemeColor(categoryInput.context, com.google.android.material.R.attr.colorOnSurface)

        listContainer.removeAllViews()
        categories.forEach { cat ->
            val tv = TextView(categoryInput.context).apply {
                text = cat
                setPadding(16, 16, 16, 16)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTextColor(textColor)
                setBackgroundColor(backgroundColor)
                setOnClickListener {
                    categoryInput.setText(cat)
                    listContainer.isVisible = false
                }
            }
            listContainer.addView(tv)
        }

        icon.setOnClickListener {
            listContainer.isVisible = !listContainer.isVisible
        }
    }

    private fun setupSearchBar() {
        val searchEditText = binding.myEditText

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    if (!filteredByDialogBooks.isNullOrEmpty()) {
                        showBooks(filteredByDialogBooks!!)
                    } else {
                        presenter.loadBooks()
                    }
                } else {
                    presenter.searchBooksByTitle(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun showNoResultsMessage(message: String) {
        binding.txtNoResults.text = message
        binding.txtNoResults.visibility = View.VISIBLE
        binding.recyclerListBooks.visibility = View.GONE
    }

    private fun setupThemeButton() {
        val btnModo = binding.myIconButton
        val imageInferior = binding.imageView2

        if (ThemeManager.isDarkMode(this)) {
            btnModo.setImageResource(R.drawable.moon_icon)
            imageInferior.setImageResource(R.drawable.desing_dark)
        } else {
            btnModo.setImageResource(R.drawable.sun_icon)
            imageInferior.setImageResource(R.drawable.desing_lower)
        }

        btnModo.setOnClickListener {
            val isNowDark = ThemeManager.toggleTheme(this)
            btnModo.setImageResource(if (isNowDark) R.drawable.moon_icon else R.drawable.sun_icon)
            imageInferior.setImageResource(if (isNowDark) R.drawable.desing_dark else R.drawable.desing_lower)
        }
    }

    private fun setupCartButton() {
        binding.imageShopBook.setOnClickListener {
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            val userEmail = user?.email ?: "guest"
            CartManager.loadCart(this, userEmail)

            val cartItems = CartManager.getCart(userEmail)

            if (cartItems.isEmpty()) {
                showEmptyCartDialog()
            } else {
                startActivity(Intent(this, ShoppingCartActivity::class.java))
            }
        }

    }

    override fun showBooks(bookList: List<Book>) {
        binding.txtNoResults.visibility = View.GONE
        binding.recyclerListBooks.visibility = View.VISIBLE

        val oldSize = filteredBooks.size

        if (oldSize > 0) {
            filteredBooks.clear()
            booksAdapter.notifyItemRangeRemoved(0, oldSize)
        }
        filteredBooks.addAll(bookList)
        booksAdapter.notifyItemRangeInserted(0, bookList.size)

        if (binding.myEditText.text.toString().trim().isEmpty()) {
            filteredByDialogBooks = bookList.toMutableList()
        }
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showEmptyResults(originalBooks: List<Book>) {
        val oldSize = filteredBooks.size

        if (oldSize > 0) {
            filteredBooks.clear()
            booksAdapter.notifyItemRangeRemoved(0, oldSize)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Sin resultados")
            .setMessage("No se encontraron libros con ese filtro")
            .setPositiveButton("Aceptar") { dialogInterface, _ ->
                filteredBooks.addAll(originalBooks)
                booksAdapter.notifyItemRangeInserted(0, originalBooks.size)
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(getColor(R.color.blue))
    }
}