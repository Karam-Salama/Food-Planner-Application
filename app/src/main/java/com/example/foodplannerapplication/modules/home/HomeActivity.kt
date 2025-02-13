package com.example.foodplannerapplication.modules.home

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.database.cache.CacheHelper
import com.example.foodplannerapplication.core.utils.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    // ================================= Global Variables ==========================================
    private lateinit var tvUserName: TextView
    private lateinit var edtSearch: EditText

    // ================================= onCreate() ================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        initViews()
    }

    // ================================= initViews  ================================================
    private fun initViews() {
        tvUserName = findViewById(R.id.tv_userName)
        edtSearch = findViewById(R.id.et_search)
    }

    // ================================= onStart() ================================================
    override fun onStart() {
        super.onStart()
        displayUserName()
    }

    // ================================= displayUserName() =========================================
    private fun displayUserName() {
        val currentUser = Firebase.auth.currentUser
        Log.d(
            "HomeActivity",
            "================ onStart: ${currentUser?.displayName} ====================="
        )
        tvUserName.text = currentUser?.displayName ?: "Guest"
    }

}