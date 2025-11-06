package com.example.myapplication.presenter.register

interface RegisterView {
    fun showNameError(message: String)
    fun showEmailError(message: String)
    fun showPasswordError(message: String)
    fun clearErrors()
    fun showSuccess(message: String)
    fun navigateToLogin()

}
