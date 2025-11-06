package com.example.myapplication.presenter.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.BookAdapter
import com.example.myapplication.databinding.ActivityHomeBinding
import com.example.myapplication.model.Book
import com.example.myapplication.presenter.base.BaseActivity
import com.example.myapplication.presenter.catalogBooks.CatalogBooksActivity
import com.example.myapplication.presenter.favorite.FavoriteBookActivity
import com.example.myapplication.presenter.login.LoginActivity
import com.example.myapplication.presenter.profile.ProfileActivity
import com.example.myapplication.presenter.shopping.ShoppingCartActivity
import com.example.myapplication.utils.ThemeManager
import com.google.firebase.auth.FirebaseAuth
import com.tuapp.utils.Verification

import androidx.activity.OnBackPressedCallback
import com.example.myapplication.presenter.shopping.manager.CartManager

class HomeActivity : BaseActivity(), HomeView {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var presenter: HomePresenter
    private val auth = FirebaseAuth.getInstance()
    private lateinit var booksAdapter: BookAdapter
    private lateinit var filteredBooks: MutableList<Book>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeManager.applyTheme(this)
        presenter = HomePresenter(this)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupVerificationDialog()

        binding.myEditText.addTextChangedListener { text: Editable? ->
            val query = text.toString().trim()
            updateHomeUIBasedOnSearch(query)
        }

        presenter = HomePresenter(this)
        setupListeners()
        setupThemeButton()

        val user = auth.currentUser
        val name = user?.displayName ?: user?.email ?: "Usuario"
        binding.txtBienvenida.text = "Bienvenido, $name"

        setupStayAtHome()

        presenter.loadBooks()

        setupListeners()
        setupThemeButton()
        setupBooksRecyclerView()
        setupSearchBar()
    }

    override fun onStart() {
        super.onStart()

        val user = auth.currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            user.reload().addOnCompleteListener {
                if (user.isEmailVerified) {
                    val name = user.displayName ?: user.email ?: "Usuario"
                    binding.txtBienvenida.text = "Bienvenido, $name"
                } else {
                    Verification.showVerificationDialog(this)
                }
            }
        }
    }

    private fun setupVerificationDialog() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            it.reload().addOnCompleteListener { _ ->
                val refreshedUser = FirebaseAuth.getInstance().currentUser
                if (refreshedUser?.isEmailVerified == false) {
                    Verification.checkEmailVerification(
                        this,
                        onVerified = {
                            Toast.makeText(this, "Correo verificado", Toast.LENGTH_SHORT).show()
                        },
                        onUnverified = {
                            Log.d("Verification", "El usuario aún no ha verificado su correo.")
                        }
                    )
                }
            }
        }
    }

    private fun setupStayAtHome() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            }
        )
    }

    private fun setupListeners() = with(binding) {
        cardUserButton.setOnClickListener {
            startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
        }
        cardBooksButton.setOnClickListener {
            startActivity(Intent(this@HomeActivity, CatalogBooksActivity::class.java))
        }
        cardTopBook.setOnClickListener {
            presenter.onTopBooksClicked()
        }
        cardFavoriteBook.setOnClickListener {
            startActivity(Intent(this@HomeActivity, FavoriteBookActivity::class.java))
        }
        imageShopBook.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val userEmail = user?.email ?: "guest"

            CartManager.loadCart(this@HomeActivity, userEmail)
            val cartItems = CartManager.getCart(userEmail)

            if (cartItems.isEmpty()) {
                showEmptyCartDialog()
            } else {
                startActivity(Intent(this@HomeActivity, ShoppingCartActivity::class.java))
            }
        }


        myMaterialIconButton.setOnClickListener {
            showExitDialog()
        }
    }

    override fun showWelcomeMessage(message: String) {
        binding.txtBienvenida.text = message
    }

    override fun navigateToProfile(nombre: String?, correo: String?, clave: String?) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    override fun navigateToCatalog() {
        startActivity(Intent(this, CatalogBooksActivity::class.java))
    }

    override fun navigateToTopBooks() {
        startActivity(
            Intent(
                this,
                com.example.myapplication.presenter.topBooks.TopBooksActivity::class.java
            )
        )
    }

    override fun navigateToFavorites() {
        startActivity(Intent(this, FavoriteBookActivity::class.java))
    }

    override fun navigateToCart(isEmpty: Boolean) {
        startActivity(Intent(this, ShoppingCartActivity::class.java))
    }

    private fun setupThemeButton() {
        if (ThemeManager.isDarkMode(this)) {
            binding.myIconButton.setImageResource(R.drawable.moon_icon)
            binding.imageView2.setImageResource(R.drawable.desing_dark)
        } else {
            binding.myIconButton.setImageResource(R.drawable.sun_icon)
            binding.imageView2.setImageResource(R.drawable.desing_lower)
        }

        binding.myIconButton.setOnClickListener {
            val isNowDark = ThemeManager.toggleTheme(this)
            binding.myIconButton.setImageResource(if (isNowDark) R.drawable.moon_icon else R.drawable.sun_icon)
            binding.imageView2.setImageResource(if (isNowDark) R.drawable.desing_dark else R.drawable.desing_lower)
        }
    }

    private fun setupBooksRecyclerView() {
        val recyclerView = binding.recyclerListBooks
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        filteredBooks = mutableListOf()
        booksAdapter = BookAdapter(filteredBooks)
        recyclerView.adapter = booksAdapter

        binding.recyclerListBooks.visibility = View.GONE
        binding.txtNoResults.visibility = View.GONE
    }

    override fun showBooks(bookList: List<Book>) {
        val oldSize = filteredBooks.size

        if (oldSize > 0) {
            filteredBooks.clear()
            booksAdapter.notifyItemRangeRemoved(0, oldSize)
        }

        if (bookList.isNotEmpty()) {
            filteredBooks.addAll(bookList)
            booksAdapter.notifyItemRangeInserted(0, bookList.size)
            binding.recyclerListBooks.visibility = View.VISIBLE
            binding.txtNoResults.visibility = View.GONE
        } else {
            binding.recyclerListBooks.visibility = View.GONE
            binding.txtNoResults.visibility = View.VISIBLE
        }
    }

    private fun setupSearchBar() {
        val searchEditText = binding.myEditText

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                updateHomeUIBasedOnSearch(query)

                if (query.isNotEmpty()) {
                    presenter.searchBooksByTitle(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateHomeUIBasedOnSearch(text: String) {
        if (text.isNotEmpty()) {
            binding.gridLayoutHome.visibility = View.GONE
            binding.frameLayoutHome.visibility = View.GONE
        } else {
            binding.recyclerListBooks.visibility = View.GONE
            binding.txtNoResults.visibility = View.GONE
            binding.gridLayoutHome.visibility = View.VISIBLE
            binding.frameLayoutHome.visibility = View.VISIBLE
        }
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}