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
import com.example.foodplannerapplication.modules.auth.data.repos.AuthRepo
import com.example.foodplannerapplication.modules.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity() : AppCompatActivity() {
    private lateinit var edt_Name:EditText
    private lateinit var edt_Email:EditText
    private lateinit var edt_Password:EditText
    private lateinit var edt_Phone:EditText

    private lateinit var tv_login:TextView
    private lateinit var btn_signUp:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Initialize views
        edt_Name = findViewById(R.id.edt_Name)
        edt_Phone = findViewById(R.id.edt_Phone)
        edt_Email = findViewById(R.id.edt_Email)
        edt_Password = findViewById(R.id.edt_Password)
        btn_signUp = findViewById(R.id.btn_signUp)
        tv_login = findViewById(R.id.tv_login)

        // set onClickListeners
        tv_login.setOnClickListener {
            finish()
        }

        btn_signUp.setOnClickListener{
            createUserWithEmailAndPassword(
                email = edt_Email.text.toString().trim(),
                password = edt_Password.text.toString().trim(),
            )
        }
    }

    private fun createUserWithEmailAndPassword(email: String, password: String) {
        val TAG = "RegisterActivity"
        Firebase.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(
                        baseContext,
                        "Sign up successfully.",
                        Toast.LENGTH_LONG,
                    ).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Sign up failed.",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }
    }
}