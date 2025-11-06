package com.tuapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

object Verification {

    fun checkEmailVerification(
        activity: Activity,
        onVerified: (() -> Unit)? = null,
        onUnverified: (() -> Unit)? = null
    ) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Toast.makeText(activity, "No hay usuario autenticado.", Toast.LENGTH_LONG).show()
            Log.w("Verification", "Usuario nulo al verificar.")
            onUnverified?.invoke()
            return
        }

        user.reload().addOnSuccessListener {
            if (user.isEmailVerified) {
                onVerified?.invoke()
            } else {
                showVerificationDialog(activity)
                onUnverified?.invoke()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(activity, "Error al verificar: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("Verification", "Error al recargar usuario: ${e.message}")
        }
    }

    fun showVerificationDialog(activity: Activity) {
        if (activity.isFinishing || activity.isDestroyed) {
            return
        }

        activity.runOnUiThread {
            try {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("🔐 VERIFICACIÓN DE CUENTA")
                builder.setMessage("Debes verificar tu correo antes de continuar.\nPor favor revisa tu bandeja de entrada de Spam.")
                builder.setPositiveButton("ABRIR GMAIL") { _, _ ->
                    try {
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.addCategory(Intent.CATEGORY_APP_EMAIL)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        activity.startActivity(intent)
                    } catch (e: Exception) {
                        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://mail.google.com/"))
                        activity.startActivity(webIntent)
                    }
                }
                builder.setNegativeButton("Más tarde", null)

                val dialog = builder.create()
                dialog.window?.setGravity(Gravity.TOP)
                dialog.show()
            } catch (e: Exception) {
                Log.e("Verification", "Error al mostrar diálogo: ${e.message}")
            }
        }
    }
}
