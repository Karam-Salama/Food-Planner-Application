package com.example.foodplannerapplication.modules.onboarding.view

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.model.cache.sharedprefs.CacheHelper
import com.example.foodplannerapplication.core.utils.functions.hideStatusAndNavBar.hideStatusAndNavBar
import com.example.foodplannerapplication.core.utils.classes.Constants
import com.example.foodplannerapplication.modules.auth.view.LoginActivity
import com.example.foodplannerapplication.modules.home.HomeActivity

class OnboardingActivity : AppCompatActivity() {
    private lateinit var startButton: Button
    private lateinit var rotatedImage: ImageView
    private lateinit var rotateAnimation: Animation

    private lateinit var tvSkip: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)

        // hide status bar and navigation bar
        hideStatusAndNavBar()

        rotatedImage = findViewById(R.id.imageView_dish3)
        startButton = findViewById(R.id.startButton)
        tvSkip = findViewById(R.id.tv_skip)

        // add animation file to image
        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        rotatedImage.startAnimation(rotateAnimation)

        // start register activity & save data in shared preferences
        startButton.setOnClickListener {
            CacheHelper.saveData(Constants.OnBoarding_KEY, true)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        tvSkip.setOnClickListener() {
            CacheHelper.saveData(Constants.OnBording_SKIP_KEY, true)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}