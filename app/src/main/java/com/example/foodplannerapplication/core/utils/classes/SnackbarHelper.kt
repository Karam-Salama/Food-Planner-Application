package com.example.foodplannerapplication.core.utils.classes

import android.view.View
import androidx.core.content.ContextCompat
import com.example.foodplannerapplication.R
import com.google.android.material.snackbar.Snackbar

object SnackbarHelper {

    private var currentSnackbar: Snackbar? = null

    fun showSnackbar(
        view: View,
        message: String,
        duration: Int = Snackbar.LENGTH_LONG,
        backgroundColor: Int = R.color.primary,
        textColor: Int = android.R.color.white,
        actionText: String? = null,
        actionColor: Int = android.R.color.white,
        actionListener: (() -> Unit)? = null
    ) {
        dismissCurrentSnackbar()

        currentSnackbar = Snackbar.make(view, message, duration).apply {
            setBackgroundTint(ContextCompat.getColor(context, backgroundColor))
            setTextColor(ContextCompat.getColor(context, textColor))

            actionText?.let { text ->
                setAction(text) {
                    actionListener?.invoke()
                }
                setActionTextColor(ContextCompat.getColor(context, actionColor))
            }

            show()
        }
    }

    fun showLoadingSnackbar(
        view: View,
        message: String,
        backgroundColor: Int = R.color.primary,
        textColor: Int = android.R.color.white
    ) {
        showSnackbar(
            view = view,
            message = message,
            duration = Snackbar.LENGTH_INDEFINITE,
            backgroundColor = backgroundColor,
            textColor = textColor
        )
    }

    fun showErrorSnackbar(
        view: View,
        message: String,
        actionText: String? = "Retry",
        actionListener: (() -> Unit)? = null
    ) {
        showSnackbar(
            view = view,
            message = message,
            backgroundColor = R.color.red,
            textColor = android.R.color.white,
            actionText = actionText,
            actionListener = actionListener
        )
    }

    fun dismissCurrentSnackbar() {
        currentSnackbar?.dismiss()
        currentSnackbar = null
    }
}