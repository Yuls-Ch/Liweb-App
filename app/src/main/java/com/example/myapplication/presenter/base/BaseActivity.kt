package com.example.myapplication.presenter.base

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.DialogExitBinding
import com.example.myapplication.databinding.DialogProductNotSelectedBinding
import com.example.myapplication.presenter.home.HomeActivity
import com.example.myapplication.presenter.login.LoginActivity
import com.example.myapplication.utils.SessionManager
import com.google.android.material.button.MaterialButton

open class BaseActivity : AppCompatActivity(), BaseView {
    protected lateinit var basePresenter: BasePresenter

    override fun onStart() {
        super.onStart()
        basePresenter = BasePresenter(this)
    }

    fun setupReturnLoginButton(button: MaterialButton) {
        button.setOnClickListener {
            basePresenter.onExitButtonClicked()
        }
    }

    override fun showExitDialog() {
        if (isFinishing || isDestroyed) return

        val dialog = Dialog(this)
        val binding = DialogExitBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        binding.btnNot.setOnClickListener { dialog.dismiss() }

        binding.btnYes.setOnClickListener {

            val sessionManager = SessionManager(this)
            sessionManager.clearSession()

            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val oneTapClient = com.google.android.gms.auth.api.identity.Identity.getSignInClient(this)

            auth.signOut()
            oneTapClient.signOut()
                .addOnCompleteListener {
                    dialog.dismiss()
                    navigateToLogin()
                }
        }


        dialog.show()
    }


    override fun showEmptyCartDialog() {
        if (isFinishing || isDestroyed) return

        val dialog = Dialog(this)
        val binding = DialogProductNotSelectedBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)

        dialog.show()
    }

    override fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}