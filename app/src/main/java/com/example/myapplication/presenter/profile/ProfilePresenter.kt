package com.example.myapplication.presenter.profile

import com.google.firebase.auth.FirebaseAuth

class ProfilePresenter(private val view: ProfileView) {

    private val auth = FirebaseAuth.getInstance()

    fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            view.showUserData(user.displayName ?: "", user.email ?: "", "")
        } else {
            view.showMessage("No hay usuario autenticado.")
        }
    }

    fun onReturnHomeClicked() {
        view.navigateToHome()
    }



}
