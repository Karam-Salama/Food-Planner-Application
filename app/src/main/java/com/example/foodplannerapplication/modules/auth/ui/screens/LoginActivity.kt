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
import com.example.foodplannerapplication.modules.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var tv_signup: TextView
    private lateinit var tv_forgetPassword: TextView
    private lateinit var edt_Email: EditText
    private lateinit var edt_Password: EditText
    private lateinit var btn_login: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        tv_signup = findViewById(R.id.tv_signup)
        tv_forgetPassword = findViewById(R.id.tv_forgetPassword)
        edt_Email = findViewById(R.id.edt_Email)
        edt_Password = findViewById(R.id.edt_Password)
        btn_login = findViewById(R.id.btn_login)

        tv_signup.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tv_forgetPassword.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }

        btn_login.setOnClickListener{
            signInWithEmailAndPassword(
                edt_Email.text.toString().trim(),
                edt_Password.text.toString().trim(),
            )
        }

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