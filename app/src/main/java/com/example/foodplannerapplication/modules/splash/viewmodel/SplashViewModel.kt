package com.example.foodplannerapplication.modules.splash.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.modules.splash.model.SplashRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashViewModel(private val repository: SplashRepository) : ViewModel() {

    sealed class NavigationDestination {
        object Onboarding : NavigationDestination()
        object Login : NavigationDestination()
        object Home : NavigationDestination()
    }

    private val _navigationEvent = MutableLiveData<NavigationDestination?>()
    val navigationEvent: LiveData<NavigationDestination?> = _navigationEvent

    fun determineDestination() {
        viewModelScope.launch {
            delay(3000)

            _navigationEvent.postValue(
                if (repository.shouldShowOnboarding()){
                    NavigationDestination.Onboarding
                } else {
                    if (!repository.isUserLoggedIn()){
                        NavigationDestination.Login
                    } else {
                        if (!repository.isEmailVerified()){
                            NavigationDestination.Login
                        } else {
                            NavigationDestination.Home
                        }
                    }
                }
            )
        }
    }
}

class SplashViewModelFactory(private val repository: SplashRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SplashViewModel(repository) as T
    }
}
