package com.example.foodplannerapplication.modules.auth.view

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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgetPasswordActivity : AppCompatActivity() {
    private lateinit var btnResetPassword: Button
    private lateinit var etEmail: TextInputEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forget_password)

        btnResetPassword = findViewById(R.id.btn_resetPassword)
        etEmail = findViewById(R.id.edt_Email)

        btnResetPassword.setOnClickListener {
            if(validateInput()){
                forgetPassword(etEmail.text.toString().trim())
            }
        }
    }
    private fun validateInput(): Boolean {
        val email = etEmail.text.toString().trim()
        var isValid = true

        val emailError = Validation.validateEmail(email)
        etEmail.error = emailError
        if (emailError != null) isValid = false

        return isValid
    }
    private fun forgetPassword(email: String) {
        val TAG = "ForgetPasswordActivity"
        Firebase.auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "resetPassword:success")
                    Toast.makeText(
                        baseContext,
                        "Password reset email sent. Check your inbox.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Log.w(TAG, "resetPassword:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Failed to send reset email. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}