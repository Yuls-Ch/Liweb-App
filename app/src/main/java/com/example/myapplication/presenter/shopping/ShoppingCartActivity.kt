package com.example.myapplication.presenter.shopping

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.myapplication.R
import com.example.myapplication.adapter.CartAdapter
import com.example.myapplication.databinding.ActivityShoppingCartBinding
import com.example.myapplication.databinding.DialogProductNotSelectedBinding
import com.example.myapplication.model.Book
import com.example.myapplication.presenter.base.BaseActivity
import com.example.myapplication.presenter.catalogBooks.CatalogBooksActivity
import com.example.myapplication.presenter.shopping.manager.CartManager
import com.example.myapplication.utils.ThemeManager
import com.google.firebase.auth.FirebaseAuth
import com.example.myapplication.BuildConfig
import com.stripe.android.PaymentConfiguration
import com.stripe.android.Stripe
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.tuapp.utils.Verification
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.*


class ShoppingCartActivity : BaseActivity(), ShoppingCartView {

    private lateinit var presenter: ShoppingCartPresenter
    private lateinit var binding: ActivityShoppingCartBinding
    private lateinit var userEmail: String

    private lateinit var paymentSheet: PaymentSheet
    private lateinit var stripe: Stripe
    private val publishableKey = BuildConfig.STRIPE_PUBLISHABLE_KEY
    private val secretKey = BuildConfig.STRIPE_SECRET_KEY


    override fun onCreate(savedInstanceState: Bundle?) {
        PaymentConfiguration.init(applicationContext, publishableKey)
        ThemeManager.applyTheme(this)

        super.onCreate(savedInstanceState)
        binding = ActivityShoppingCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = ShoppingCartPresenter(this, this)
        paymentSheet = PaymentSheet(this) { result ->
            when (result) {
                is PaymentSheetResult.Completed -> {
                    mostrarSweetExito()
                }

                is PaymentSheetResult.Canceled -> {
                    Toast.makeText(this, "Pago cancelado", Toast.LENGTH_SHORT).show()
                }

                is PaymentSheetResult.Failed -> {
                    Toast.makeText(this, "Error: ${result.error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        setupVerificationDialog()
        setupCartUI()
        setupExitButton()
        setupThemeButton()
        setupReturnCatalogBooks()
        setupBuyButton()
    }

    private fun setupVerificationDialog() {
        val user = FirebaseAuth.getInstance().currentUser
        binding.ButtonComprar.isEnabled = false

        user?.reload()?.addOnCompleteListener {
            val refreshedUser = FirebaseAuth.getInstance().currentUser

            if (refreshedUser?.isEmailVerified == true) {
                binding.ButtonComprar.isEnabled = true
                binding.ButtonComprar.alpha = 1f
                binding.ButtonComprar.setOnClickListener {
                    setupBuyButton()
                }

            } else {
                binding.ButtonComprar.isEnabled = false
                binding.ButtonComprar.alpha = 0.5f
                binding.ButtonComprar.setOnClickListener {
                    Toast.makeText(this, "Verifica tu correo antes de comprar", Toast.LENGTH_SHORT).show()
                    Verification.checkEmailVerification(this)
                }
            }
        }
    }

    private fun setupExitButton() {
        setupReturnLoginButton(binding.btnExit)
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

    private fun setupCartUI() {
        val user = FirebaseAuth.getInstance().currentUser
        userEmail = user?.email ?: "guest"

        CartManager.loadCart(this, userEmail)
        println("📦 Cargando carrito de: $userEmail")

        binding.booksShooppingRecycler.layoutManager = LinearLayoutManager(this)
        presenter.loadCart(userEmail)
    }

    private fun setupReturnCatalogBooks() {
        binding.materialButton2.setOnClickListener {
            val intent = Intent(this, CatalogBooksActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBuyButton() {
        binding.ButtonComprar.setOnClickListener {
            val totalText = binding.tvQuantitytotal.text.toString().replace("S/", "").trim()
            val total = totalText.toDoubleOrNull()

            if (total == null || total <= 0.0) {
                Toast.makeText(this, "No hay productos en el carrito", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            presenter.onBuyButtonClicked(total)
        }
    }

    override fun showCartItem(cartList: List<Book>) {
        binding.booksShooppingRecycler.adapter = CartAdapter(cartList.toMutableList(), userEmail) {
            presenter.loadCart(userEmail)
        }
    }

    override fun showTotals(subtotal: Double, total: Double) {
        binding.tvQuantity.text = String.format(Locale.getDefault(), "S/ %.2f", subtotal)
        binding.tvQuantitytotal.text = String.format(Locale.getDefault(), "S/ %.2f", total)
    }

    @SuppressLint("SetTextI18n")
    override fun showEmptyCartMessage() {
        val dialogBinding = DialogProductNotSelectedBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        binding.tvQuantity.text = "S/ 0.00"
        binding.tvQuantitytotal.text = "S/ 0.00"
    }

    override fun iniciarPago(total: Double) {
        stripe = Stripe(this, publishableKey)

        Thread {
            try {
                val client = OkHttpClient()
                val formBody = FormBody.Builder()
                    .add("amount", (total * 100).toInt().toString())
                    .add("currency", "pen")
                    .add("payment_method_types[]", "card")
                    .build()

                val request = Request.Builder()
                    .url("https://api.stripe.com/v1/payment_intents")
                    .addHeader("Authorization", "Bearer $secretKey")
                    .post(formBody)
                    .build()

                val response = client.newCall(request).execute()
                val json = JSONObject(response.body?.string() ?: "")
                val clientSecret = json.getString("client_secret")

                runOnUiThread {
                    paymentSheet.presentWithPaymentIntent(
                        clientSecret,
                        PaymentSheet.Configuration("Liweb - Compra de Libros")
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error al crear el pago", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun mostrarSweetExito() {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("¡Pago exitoso!")
            .setContentText("Gracias por tu compra")
            .setConfirmText("OK")
            .setConfirmClickListener { dialog ->
                presenter.descontarStockFirebase(userEmail)
                presenter.clearCart(userEmail, mostrarDialog = false)

                setResult(RESULT_OK)

                dialog.dismissWithAnimation()
                finish()
            }
            .show()
    }
}