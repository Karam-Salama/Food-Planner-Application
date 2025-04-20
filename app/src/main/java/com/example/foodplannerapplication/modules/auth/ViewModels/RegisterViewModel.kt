package com.example.foodplannerapplication.modules.auth.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.modules.auth.models.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _registrationState = MutableLiveData<RegistrationState>()
    val registrationState: LiveData<RegistrationState> = _registrationState

    sealed class RegistrationState {
        data class Loading(val message: String = "Loading...") : RegistrationState()
        data class Success(val user: FirebaseUser, val message: String) : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    fun registerUser(
        email: String,
        password: String,
        fullName: String,
        phone: String? = null
    ) {
        _registrationState.value = RegistrationState.Loading("Registering your account...")
        viewModelScope.launch {
            val result = authRepository.registerWithEmail(email, password, fullName, phone)
            _registrationState.postValue(
                if (result.isSuccess) {
                    RegistrationState.Success(
                        user = result.getOrNull()!!,
                        message = "Please check your email to verify your account"
                    )
                } else {
                    RegistrationState.Error(
                        result.exceptionOrNull()?.message ?: "Registration failed"
                    )
                }
            )
        }
    }
}

class AuthViewModelFactory( private val repository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}