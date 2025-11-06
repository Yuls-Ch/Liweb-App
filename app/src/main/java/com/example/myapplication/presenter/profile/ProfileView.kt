package com.example.myapplication.presenter.profile

import com.example.myapplication.presenter.base.BaseView

interface ProfileView : BaseView {
    fun showUserData(nombre: String, correo: String, clave: String)
    fun showMessage(message: String)
}