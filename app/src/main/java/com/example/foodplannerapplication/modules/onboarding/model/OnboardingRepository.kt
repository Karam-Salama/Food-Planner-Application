package com.example.foodplannerapplication.modules.onboarding.model

import com.example.foodplannerapplication.core.data.cache.CacheHelper
import com.example.foodplannerapplication.core.utils.Constants

class OnboardingRepository(private val cacheHelper: CacheHelper) {
    suspend fun setOnboardingCompleted() {
        cacheHelper.saveData(Constants.OnBoarding_KEY, true)
    }

    suspend fun setOnboardingSkipped() {
        cacheHelper.saveData(Constants.OnBording_SKIP_KEY, true)
    }
}