package com.example.foodplannerapplication.modules.splash.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.foodplannerapplication.modules.onboarding.view.OnboardingActivity
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.model.cache.sharedprefs.CacheHelper
import com.example.foodplannerapplication.core.utils.functions.hideStatusAndNavBar.hideStatusAndNavBar
import com.example.foodplannerapplication.core.utils.classes.Constants
import com.example.foodplannerapplication.modules.auth.view.LoginActivity
import com.example.foodplannerapplication.modules.home.HomeActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // Hide status bar and navigation bar
        hideStatusAndNavBar()

        // Initialize CacheHelper
        CacheHelper.init(this)
        val isOnboardingCompleted = CacheHelper.getBoolean(Constants.OnBoarding_KEY, false)

        if (isOnboardingCompleted) {
            // Check if the user is logged in and email is verified
            if (Firebase.auth.currentUser != null) {
                delayedNavigate(HomeActivity::class.java)
            } else {
                delayedNavigate(LoginActivity::class.java)
            }
        } else {
            // Navigate to Onboarding if not completed
            delayedNavigate(OnboardingActivity::class.java)
        }
    }

    private fun delayedNavigate(destination: Class<*>, delayMillis: Long = 3000) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, destination))
            finish()
        }, delayMillis)
    }
}
