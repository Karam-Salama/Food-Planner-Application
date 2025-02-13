package com.example.foodplannerapplication.modules.auth.ui.screens

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
import com.example.foodplannerapplication.core.database.cache.CacheHelper
import com.example.foodplannerapplication.core.functions.Validation
import com.example.foodplannerapplication.core.utils.Constants
import com.example.foodplannerapplication.modules.home.HomeActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var edtFullName: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var tvLogin: TextView
    private lateinit var btnSignUp: Button
    private lateinit var errorName: TextView
    private lateinit var errorPhone: TextView
    private lateinit var errorEmail: TextView
    private lateinit var errorPassword: TextView
    private lateinit var errorConfirmPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize views
        edtFullName = findViewById(R.id.edt_Name)
        edtPhone = findViewById(R.id.edt_Phone)
        edtEmail = findViewById(R.id.edt_Email)
        edtPassword = findViewById(R.id.edt_Password)
        edtConfirmPassword = findViewById(R.id.edt_confirmPassword)
        btnSignUp = findViewById(R.id.btn_signUp)
        tvLogin = findViewById(R.id.tv_login)

        // Initialize error views
        errorName = findViewById(R.id.error_name)
        errorPhone = findViewById(R.id.error_phone)
        errorEmail = findViewById(R.id.error_email)
        errorPassword = findViewById(R.id.error_password)
        errorConfirmPassword = findViewById(R.id.error_confirmPassword)

        // Set onClickListeners
        tvLogin.setOnClickListener { finish() }

        btnSignUp.setOnClickListener {
            if (validateInputs()) {
                createUserWithEmailAndPassword(
                    email = edtEmail.text.toString().trim(),
                    password = edtPassword.text.toString().trim()
                )
            }
        }
    }

    private fun validateInputs(): Boolean {
        val fullName = edtFullName.text.toString().trim()
        val phone = edtPhone.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val confirmPassword = edtConfirmPassword.text.toString().trim()

        var isValid = true

        val nameError = Validation.validateName(fullName)
        errorName.text = nameError
        if (nameError != null) isValid = false

        val phoneError = Validation.validatePhone(phone)
        errorPhone.text = phoneError
        if (phoneError != null) isValid = false

        val emailError = Validation.validateEmail(email)
        errorEmail.text = emailError
        if (emailError != null) isValid = false

        val passwordError = Validation.validatePassword(password)
        errorPassword.text = passwordError
        if (passwordError != null) isValid = false

        val confirmPasswordError = Validation.validateConfirmPassword(confirmPassword, password)
        errorConfirmPassword.text = confirmPasswordError
        if (confirmPasswordError != null) isValid = false

        return isValid
    }

    private fun createUserWithEmailAndPassword(email: String, password: String) {
        val fullName = edtFullName.text.toString().trim()
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
