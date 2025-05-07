package com.example.foodplannerapplication.modules.auth.view
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.helpers.DialogHelper
import com.example.foodplannerapplication.core.helpers.SnackbarHelper
import com.example.foodplannerapplication.modules.auth.ViewModels.ResetPasswordViewModel
import com.example.foodplannerapplication.modules.auth.ViewModels.ResetPasswordViewModelFactory
import com.example.foodplannerapplication.modules.auth.models.AuthRepository
import com.google.android.material.textfield.TextInputEditText

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var btnResetPassword: Button
    private lateinit var etEmail: TextInputEditText
    private lateinit var viewModel: ResetPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_password)
        initViews()
        setupViewModel()
        observeResetState()
        setupListeners()
    }

    private fun initViews() {
        btnResetPassword = findViewById(R.id.btn_resetPassword)
        etEmail = findViewById(R.id.edt_Email)
    }

    private fun setupViewModel() {
        val repository = AuthRepository()
        viewModel = ViewModelProvider(this, ResetPasswordViewModelFactory(repository))
            .get(ResetPasswordViewModel::class.java)
    }

    private fun observeResetState() {
        viewModel.resetState.observe(this) { state ->
            when (state) {
                is ResetPasswordViewModel.ResetPasswordState.Loading -> {
                    SnackbarHelper.showLoadingSnackbar(
                        findViewById(android.R.id.content),
                        state.message
                    )
                }
                is ResetPasswordViewModel.ResetPasswordState.Success -> {
                    SnackbarHelper.dismissCurrentSnackbar()
                    DialogHelper.showGenericDialog(
                        context = this,
                        message = state.message,
                        positiveButtonText = "OK",
                        onPositiveAction = {
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        },
                        cancelable = false // لمنع إغلاق الـ Dialog بالضغط خارجها
                    )
                }
                is ResetPasswordViewModel.ResetPasswordState.Error -> {
                    SnackbarHelper.dismissCurrentSnackbar()
                    if (state.message.contains("No account")) {
                        etEmail.error = state.message
                    } else {
                        SnackbarHelper.showErrorSnackbar(
                            findViewById(android.R.id.content),
                            state.message,
                            "Retry",
                            { retryResetPassword() }
                        )
                    }
                }
            }
        }
    }

    private fun retryResetPassword() {
        if (validateInput()) {
            viewModel.resetPassword(etEmail.text.toString().trim())
        }
    }

    private fun setupListeners() {
        btnResetPassword.setOnClickListener {
            if (validateInput()) {
                viewModel.resetPassword(etEmail.text.toString().trim())
            }
        }
    }

    private fun validateInput(): Boolean {
        val email = etEmail.text.toString().trim()
        var isValid = true

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid email format"
            isValid = false
        }

        return isValid
    }
}