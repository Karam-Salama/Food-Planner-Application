package com.example.foodplannerapplication.modules.auth.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.utils.functions.Validation
import com.example.foodplannerapplication.modules.home.HomeActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText

    private lateinit var tvLogin: TextView
    private lateinit var btnSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize views
        etFullName = findViewById(R.id.edt_Name)
        etPhone = findViewById(R.id.edt_Phone)
        etEmail = findViewById(R.id.edt_Email)
        etPassword = findViewById(R.id.edt_Password)
        etConfirmPassword = findViewById(R.id.edt_confirmPassword)
        btnSignUp = findViewById(R.id.btn_signUp)
        tvLogin = findViewById(R.id.tv_login)

        // Set onClickListeners
        tvLogin.setOnClickListener { finish() }

        btnSignUp.setOnClickListener {
            if (validateInputs()) {
                createUserWithEmailAndPassword(
                    email = etEmail.text.toString().trim(),
                    password = etPassword.text.toString().trim()
                )
            }
        }
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

    private fun createUserWithEmailAndPassword(email: String, password: String) {
        val fullName = etFullName.text.toString().trim()
        val TAG = "RegisterActivity"

        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = Firebase.auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = fullName
                    }

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                                Toast.makeText(this, "Sign up successful.", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, HomeActivity::class.java))
                            } else {
                                Log.w(TAG, "User profile update failed.", profileTask.exception)
                            }
                        }
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Sign up failed.", Toast.LENGTH_LONG).show()
                }
            }
    }
}
