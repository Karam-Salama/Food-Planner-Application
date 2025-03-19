package com.example.foodplannerapplication.core.utils.classes

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import androidx.core.content.ContextCompat.startActivity
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.auth.view.LoginActivity
import com.example.foodplannerapplication.modules.auth.view.RegisterActivity

object DialogHelper {

    fun showLoginRequiredDialog(context: Context) {
        val builder = AlertDialog.Builder(context, R.style.CustomDialogTheme)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_login_required, null)

        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnLogin = dialogView.findViewById<Button>(R.id.btn_login)

        val dialog = builder.setView(dialogView).create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnLogin.setOnClickListener {
            // الانتقال إلى شاشة تسجيل الدخول
            startActivity(context, Intent(context, LoginActivity::class.java), null)
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun showDeleteConfirmationDialog(context: Context, onConfirmDelete: () -> Unit) {
        val builder = AlertDialog.Builder(context, R.style.CustomDialogTheme)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_delete_confirmation, null)

        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnDelete = dialogView.findViewById<Button>(R.id.btn_delete)

        val dialog = builder.setView(dialogView).create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnDelete.setOnClickListener {
            onConfirmDelete.invoke()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
    fun showLoginConfirmationDialog(context: Context, onConfirmLogin: () -> Unit) {
        val builder = AlertDialog.Builder(context, R.style.CustomDialogTheme)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_login_sucess, null)

        val btnOkay = dialogView.findViewById<Button>(R.id.btn_ok)

        val dialog = builder.setView(dialogView).create()

        btnOkay.setOnClickListener {
            onConfirmLogin.invoke()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun showRegisterConfirmationDialog(context: Context, onConfirmRegistered: () -> Unit) {
        val builder = AlertDialog.Builder(context, R.style.CustomDialogTheme)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_register_sucess, null)

        val btnOkay = dialogView.findViewById<Button>(R.id.btn_ok)

        val dialog = builder.setView(dialogView).create()

        btnOkay.setOnClickListener {
            onConfirmRegistered.invoke()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
    fun showForgetPasswordConfirmationDialog(context: Context, onConfirmForgetedPassword: () -> Unit) {
        val builder = AlertDialog.Builder(context, R.style.CustomDialogTheme)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_forget_password_sucess, null)

        val btnOkay = dialogView.findViewById<Button>(R.id.btn_ok)

        val dialog = builder.setView(dialogView).create()

        btnOkay.setOnClickListener {
            onConfirmForgetedPassword.invoke()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
}

