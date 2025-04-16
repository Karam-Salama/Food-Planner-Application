package com.example.foodplannerapplication.modules.onboarding.model

import com.example.foodplannerapplication.core.model.cache.sharedprefs.CacheHelper
import com.example.foodplannerapplication.core.utils.classes.Constants

class OnboardingRepository(private val cacheHelper: CacheHelper) {
    suspend fun setOnboardingCompleted() {
        cacheHelper.saveData(Constants.OnBoarding_KEY, true)
    }

    suspend fun setOnboardingSkipped() {
        cacheHelper.saveData(Constants.OnBording_SKIP_KEY, true)
    }
}