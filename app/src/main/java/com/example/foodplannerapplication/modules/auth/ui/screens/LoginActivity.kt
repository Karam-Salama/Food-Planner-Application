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
import com.example.foodplannerapplication.core.functions.Validation
import com.example.foodplannerapplication.modules.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var tvSignup: TextView
    private lateinit var tvForgetPassword: TextView
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var errorEmail: TextView
    private lateinit var errorPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        tvSignup = findViewById(R.id.tv_signup)
        tvForgetPassword = findViewById(R.id.tv_forgetPassword)
        edtEmail = findViewById(R.id.edt_Email)
        edtPassword = findViewById(R.id.edt_Password)
        btnLogin = findViewById(R.id.btn_login)


        errorEmail = findViewById(R.id.error_login_email)
        errorPassword = findViewById(R.id.error_login_password)

        tvSignup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgetPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }

        btnLogin.setOnClickListener{
            if (validateInputs()){
                signInWithEmailAndPassword(
                    edtEmail.text.toString().trim(),
                    edtPassword.text.toString().trim(),
                )
            }
        }

    }

    private fun validateInputs(): Boolean {
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()

        var isValid = true

        val emailError = Validation.validateEmail(email)
        errorEmail.text = emailError
        if (emailError != null) isValid = false

        val passwordError = Validation.validatePassword(password)
        errorPassword.text = passwordError
        if (passwordError != null) isValid = false
        return isValid
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        val TAG = "SignInActivity"
        Firebase.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    Toast.makeText(
                        baseContext,
                        "Login successfully.",
                        Toast.LENGTH_LONG,
                    ).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Login failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}