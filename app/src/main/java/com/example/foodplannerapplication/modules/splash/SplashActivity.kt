package com.example.foodplannerapplication.modules.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodplannerapplication.modules.onboarding.OnboardingActivity
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.database.cache.CacheHelper
import com.example.foodplannerapplication.core.functions.hideStatusAndNavBar.hideStatusAndNavBar
import com.example.foodplannerapplication.modules.auth.RegisterActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)


        // hide status bar and navigation bar
        hideStatusAndNavBar()

        CacheHelper.init(this)
        val isOnboardingCompleted = CacheHelper.getBoolean("welcomeVisited", false)
        if (isOnboardingCompleted) {
            delayedNavigate(RegisterActivity::class.java)
        } else {
            delayedNavigate(OnboardingActivity::class.java)
        }

    }

    fun delayedNavigate(destination: Class<*>, delayMillis: Long = 3000) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, destination))
            finish()
        }, delayMillis)
    }
}