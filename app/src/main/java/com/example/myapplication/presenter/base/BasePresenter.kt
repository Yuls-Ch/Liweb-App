package com.example.myapplication.presenter.base

open class BasePresenter(protected val view: BaseView) {
    open fun onExitButtonClicked() {
        view.showExitDialog()
    }
}