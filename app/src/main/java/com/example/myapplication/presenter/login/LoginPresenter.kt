package com.example.myapplication.presenter.login

import com.google.firebase.auth.FirebaseAuth

class LoginPresenter(private val view: LoginView) {

    private val auth = FirebaseAuth.getInstance()

    fun loginWithEmail(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            view.showToast("Completa todos los campos")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                view.showToast("Inicio de sesión exitoso")
                view.navigateToHome()
            }
            .addOnFailureListener {
                view.showToast("Error: ${it.message}")
            }
    }
}
