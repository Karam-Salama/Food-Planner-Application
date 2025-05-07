package com.example.foodplannerapplication.modules.auth.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.helpers.DialogHelper
import com.example.foodplannerapplication.core.helpers.SnackbarHelper
import com.example.foodplannerapplication.core.functions.Validation
import com.example.foodplannerapplication.modules.auth.ViewModels.AuthViewModelFactory
import com.example.foodplannerapplication.modules.auth.ViewModels.RegisterViewModel
import com.example.foodplannerapplication.modules.auth.models.AuthRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var etFullName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var tvLogin: TextView
    private lateinit var btnSignUp: Button
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        setupViews()
        setupClickListeners()
        setupViewModel()
        observeRegistrationState()
    }

    private fun setupViews() {
        etFullName = findViewById(R.id.edt_Name)
        etPhone = findViewById(R.id.edt_Phone)
        etEmail = findViewById(R.id.edt_Email)
        etPassword = findViewById(R.id.edt_Password)
        etConfirmPassword = findViewById(R.id.edt_confirmPassword)
        btnSignUp = findViewById(R.id.btn_signUp)
        tvLogin = findViewById(R.id.tv_login)
    }

    private fun setupClickListeners() {
        tvLogin.setOnClickListener {
            finish()
        }

        btnSignUp.setOnClickListener {
            if (validateInputs()) {
                viewModel.registerUser(
                    email = etEmail.text.toString().trim(),
                    password = etPassword.text.toString().trim(),
                    fullName = etFullName.text.toString().trim(),
                    phone = etPhone.text.toString().trim() // إضافة رقم الهاتف
                )
            }
        }
    }

    private fun setupViewModel() {
        val repository = AuthRepository()
        viewModel = ViewModelProvider(this, AuthViewModelFactory(repository)).get(RegisterViewModel::class.java)
    }

    private fun observeRegistrationState() {
        viewModel.registrationState.observe(this) { state ->
            when (state) {
                is RegisterViewModel.RegistrationState.Loading -> {
                    showLoadingSnackbar(state.message)
                }
                is RegisterViewModel.RegistrationState.Success -> {
                    dismissLoadingSnackbar()
                    showSuccessDialog(state.message)
                }
                is RegisterViewModel.RegistrationState.Error -> {
                    dismissLoadingSnackbar()
                    showErrorSnackbar(state.message)
                }

                else -> {

                }
            }
        }
    }

    private fun showLoadingSnackbar(message: String) {
        SnackbarHelper.showLoadingSnackbar(
            view = findViewById(android.R.id.content),
            message = message
        )
    }

    private fun dismissLoadingSnackbar() {
        SnackbarHelper.dismissCurrentSnackbar()
    }

    private fun showSuccessDialog(message: String) {
        DialogHelper.showGenericDialog(
            context = this,
            message = message,
            positiveButtonText = getString(R.string.ok),
            onPositiveAction = {
                Firebase.auth.signOut()
                navigateToLogin()
            },
            cancelable = false
        )
    }

    private fun showErrorSnackbar(message: String) {
        SnackbarHelper.showErrorSnackbar(
            view = findViewById(android.R.id.content),
            message = message
        )
    }

    private fun navigateToLogin() {
        Firebase.auth.signOut()
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finishAffinity()
    }

    private fun validateInputs(): Boolean {
        val fullName = etFullName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        var isValid = true

        val nameError = Validation.validateName(fullName)
        etFullName.error = nameError
        if (nameError != null) isValid = false

        val phoneError = Validation.validatePhone(phone)
        etPhone.error = phoneError
        if (phoneError != null) isValid = false

        val emailError = Validation.validateEmail(email)
        etEmail.error = emailError
        if (emailError != null) isValid = false

        val passwordError = Validation.validatePassword(password)
        etPassword.error = passwordError
        if (passwordError != null) isValid = false

        val confirmPasswordError = Validation.validateConfirmPassword(confirmPassword, password)
        etConfirmPassword.error = confirmPasswordError
        if (confirmPasswordError != null) isValid = false

        return isValid
    }
}