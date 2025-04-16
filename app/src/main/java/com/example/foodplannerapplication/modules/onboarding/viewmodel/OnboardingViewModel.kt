package com.example.foodplannerapplication.modules.onboarding.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.core.model.cache.sharedprefs.CacheHelper
import com.example.foodplannerapplication.core.utils.classes.Constants
import com.example.foodplannerapplication.modules.onboarding.model.OnboardingRepository
import kotlinx.coroutines.launch

class OnboardingViewModel(private val repo: OnboardingRepository) : ViewModel() {
    sealed class NavigationDestination {
        object Login : NavigationDestination()
        object Home : NavigationDestination()
    }

    private val _navigationEvent = MutableLiveData<NavigationDestination?>()
    val navigationEvent: LiveData<NavigationDestination?> = _navigationEvent

    fun handleStartButtonClick() {
        viewModelScope.launch {
            repo.setOnboardingCompleted()
            _navigationEvent.postValue(NavigationDestination.Login)
        }
    }

    fun handleSkipButtonClick() {
        viewModelScope.launch {
            repo.setOnboardingSkipped()
            _navigationEvent.postValue(NavigationDestination.Home)
        }
    }
}

class OnboardingViewModelFactory(private val repository: OnboardingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            return OnboardingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}