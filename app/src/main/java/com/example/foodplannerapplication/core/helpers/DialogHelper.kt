package com.example.foodplannerapplication.core.helpers
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.auth.view.LoginActivity

object  DialogHelper {
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

    fun showLogoutDialog(context: Context, onConfirmLogout: () -> Unit){
        val builder = androidx.appcompat.app.AlertDialog.Builder(context, R.style.CustomDialogTheme)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_logout, null)

        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)

        val dialog = builder.setView(dialogView).create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            onConfirmLogout.invoke()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    fun showGenericDialog(
        context: Context,
        message: String,
        positiveButtonText: String = "OK",
        onPositiveAction: (() -> Unit)? = null,
        negativeButtonText: String? = null,
        onNegativeAction: (() -> Unit)? = null,
        cancelable: Boolean = true
    ) {
        val builder = AlertDialog.Builder(context, R.style.CustomDialogTheme)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_generic, null) // ستحتاج لإنشاء هذا الـ layout

        val tvMessage = dialogView.findViewById<TextView>(R.id.tv_message)
        val btnPositive = dialogView.findViewById<Button>(R.id.btn_positive)
        val btnNegative = dialogView.findViewById<Button>(R.id.btn_negative)

        tvMessage.text = message
        btnPositive.text = positiveButtonText

        val dialog = builder.setView(dialogView).create()

        dialog.setCancelable(cancelable)

        btnPositive.setOnClickListener {
            onPositiveAction?.invoke()
            dialog.dismiss()
        }

        if (negativeButtonText != null) {
            btnNegative.visibility = View.VISIBLE
            btnNegative.text = negativeButtonText
            btnNegative.setOnClickListener {
                onNegativeAction?.invoke()
                dialog.dismiss()
            }
        } else {
            btnNegative.visibility = View.GONE
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }
}

