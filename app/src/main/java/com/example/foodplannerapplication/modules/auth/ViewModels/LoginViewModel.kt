package com.example.foodplannerapplication.modules.auth.ViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.modules.auth.models.AuthRepository
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _loginState = MutableLiveData<LoginState>()
    val loginState: LiveData<LoginState> = _loginState

    sealed class LoginState {
        data class Loading(val message: String = "Loading...") : LoginState()
        data class Success(val message: String) : LoginState()
        data class Error(val message: String) : LoginState()
        object EmailNotVerified : LoginState()
    }

    fun loginWithEmail(email: String, password: String) {
        _loginState.value = LoginState.Loading("Signing in...")
        viewModelScope.launch {
            try {
                val result = authRepository.loginWithEmail(email, password)
                handleLoginResult(result)
            } catch (e: Exception) {
                setErrorState(e.message ?: "Login failed")
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        _loginState.value = LoginState.Loading("Signing in with Google...")
        viewModelScope.launch {
            try {
                val result = authRepository.loginWithGoogle(idToken)
                handleLoginResult(result)
            } catch (e: Exception) {
                setErrorState(e.message ?: "Google login failed")
            }
        }
    }

    fun loginWithFacebook(token: AccessToken) {
        _loginState.value = LoginState.Loading("Signing in with Facebook...")
        viewModelScope.launch {
            try {
                val result = authRepository.loginWithFacebook(token)
                handleLoginResult(result, true)
            } catch (e: Exception) {
                _loginState.postValue(LoginState.Error(e.message ?: "Facebook login failed"))
            }
        }
    }

    private fun handleLoginResult(result: Result<FirebaseUser>, isSocialLogin: Boolean = false) {
        if (result.isSuccess) {
            if (!isSocialLogin && result.getOrNull()?.isEmailVerified == false) {
                _loginState.postValue(LoginState.EmailNotVerified)
            } else {
                _loginState.postValue(LoginState.Success("Login successful"))
            }
        } else {
            _loginState.postValue(LoginState.Error(result.exceptionOrNull()?.message ?: "Login failed"))
        }
    }

    fun setErrorState(message: String) {
        _loginState.postValue(LoginState.Error(message))
    }
}

class LoginViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}