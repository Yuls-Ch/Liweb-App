package com.example.myapplication.presenter.options

class OptionsRegisterPresenter(private val view: OptionsRegisterView) {

    fun onLoginClicked() {
        view.navigateToLogin()
    }

    fun onRegisterClicked() {
        view.navigateToRegister()
    }
}