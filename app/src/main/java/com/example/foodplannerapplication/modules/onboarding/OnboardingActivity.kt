package com.example.foodplannerapplication.modules.onboarding

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.database.cache.CacheHelper
import com.example.foodplannerapplication.core.functions.hideStatusAndNavBar.hideStatusAndNavBar
import com.example.foodplannerapplication.modules.auth.RegisterActivity


class OnboardingActivity : AppCompatActivity() {
    private lateinit var startButton: Button
    private lateinit var rotatedImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)

        // hide status bar and navigation bar
        hideStatusAndNavBar()

        rotatedImage = findViewById(R.id.imageView_dish3)
        startButton = findViewById(R.id.startButton)

        // add animation file to image
        val rotateAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        rotatedImage.startAnimation(rotateAnimation)

        // start register activity & save data in shared preferences
        startButton.setOnClickListener {
            CacheHelper.saveData("welcomeVisited", true)
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }


}