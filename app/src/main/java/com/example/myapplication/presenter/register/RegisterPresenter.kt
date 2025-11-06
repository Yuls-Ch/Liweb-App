package com.example.myapplication.presenter.register

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest

class RegisterPresenter(private val view: RegisterView) {

    private val auth = FirebaseAuth.getInstance()

    fun registerUser(name: String, email: String, password: String) {
        view.clearErrors()

        if (name.isEmpty()) {
            view.showNameError("Ingresa tu nombre")
            return
        }
        if (email.isEmpty()) {
            view.showEmailError("Ingresa tu correo")
            return
        }
        if (password.length < 6) {
            view.showPasswordError("Debe tener al menos 6 caracteres")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    if (user != null) {
                        val profileUpdates = userProfileChangeRequest {
                            displayName = name
                        }

                        user.updateProfile(profileUpdates).addOnCompleteListener {
                            user.reload().addOnCompleteListener { _ ->

                                user.sendEmailVerification()
                                    .addOnCompleteListener { verifyTask ->
                                        if (verifyTask.isSuccessful) {
                                            view.showSuccess(
                                                "Usuario registrado correctamente.\n" +
                                                        "Se envió un correo de verificación a $email"
                                            )

                                            auth.signOut()
                                            view.navigateToLogin()

                                        } else {
                                            view.showEmailError(
                                                "No se pudo enviar el correo de verificación: " +
                                                        (verifyTask.exception?.message ?: "")
                                            )
                                        }
                                    }
                            }
                        }
                    }
                } else {
                    view.showEmailError(task.exception?.message ?: "Error al registrar usuario")
                }
            }
    }
}
