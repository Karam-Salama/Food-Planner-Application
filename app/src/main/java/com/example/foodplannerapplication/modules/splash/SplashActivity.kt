package com.example.foodplannerapplication.modules.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.foodplannerapplication.modules.onboarding.OnboardingActivity
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.database.cache.CacheHelper
import com.example.foodplannerapplication.core.functions.hideStatusAndNavBar.hideStatusAndNavBar
import com.example.foodplannerapplication.core.utils.Constants
import com.example.foodplannerapplication.modules.auth.RegisterActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        // hide status bar and navigation bar
        hideStatusAndNavBar()

        CacheHelper.init(this)
        val isOnboardingCompleted = CacheHelper.getBoolean(Constants.OnBoarding_KEY, false)
        if (isOnboardingCompleted) {
            delayedNavigate(RegisterActivity::class.java)
        } else {
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