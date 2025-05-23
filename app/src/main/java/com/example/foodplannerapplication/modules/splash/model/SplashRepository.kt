package com.example.foodplannerapplication.modules.splash.model
import com.example.foodplannerapplication.core.data.cache.CacheHelper
import com.example.foodplannerapplication.core.utils.Constants
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashRepository(private val cacheHelper: CacheHelper) {
    suspend fun shouldShowOnboarding(): Boolean {
        val isOnboardingCompleted = cacheHelper.getBoolean(Constants.OnBoarding_KEY, false)
        val isAuthSkipClicked = cacheHelper.getBoolean(Constants.OnBording_SKIP_KEY, false)
        return !isOnboardingCompleted && !isAuthSkipClicked
    }

    fun isUserLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun isEmailVerified(): Boolean {
        return Firebase.auth.currentUser?.isEmailVerified == true
    }
}