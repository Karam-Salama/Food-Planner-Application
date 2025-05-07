package com.example.foodplannerapplication.modules.auth.ViewModels
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.modules.auth.models.AuthRepository
import kotlinx.coroutines.launch

class ResetPasswordViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _resetState = MutableLiveData<ResetPasswordState>()
    val resetState: LiveData<ResetPasswordState> = _resetState

    sealed class ResetPasswordState {
        data class Loading(val message: String = "Sending reset email...") : ResetPasswordState()
        data class Success(val message: String) : ResetPasswordState()
        data class Error(val message: String) : ResetPasswordState()
    }

    fun resetPassword(email: String) {
        _resetState.value = ResetPasswordState.Loading()
        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                _resetState.postValue(ResetPasswordState.Success("Reset link sent to $email."))
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
                _resetState.postValue(ResetPasswordState.Error(errorMsg))
                Log.e("ResetPassword", "+++++++++++++ Error: $errorMsg +++++++++++++++")
            }
        }
    }
}

class ResetPasswordViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResetPasswordViewModel::class.java)) {
            return ResetPasswordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}