package com.example.foodplannerapplication.modules.settings.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.modules.settings.models.SettingRepo
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: SettingRepo) : ViewModel() {
    private val _userData = MutableLiveData<FirebaseUser?>()
    val userData: LiveData<FirebaseUser?> get() = _userData

    private val _updateResult = MutableLiveData<Pair<Boolean, String>>()
    val updateResult: LiveData<Pair<Boolean, String>> get() = _updateResult

    init {
        loadUserData()
    }

    fun loadUserData() {
        _userData.value = repository.getCurrentUser()
    }

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            val success = repository.updateUserName(newName)
            _updateResult.value = Pair(success,
                if (success) "Name updated successfully" else "Failed to update name")
        }
    }

    fun updateUserEmail(newEmail: String) {
        viewModelScope.launch {
            val success = repository.updateUserEmail(newEmail)
            _updateResult.value = Pair(success,
                if (success) "Email updated successfully" else "Failed to update email")
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            val success = repository.changePassword(currentPassword, newPassword)
            _updateResult.value = Pair(success,
                if (success) "Password updated successfully" else "Failed to update password")
        }
    }

    fun logout() {
        repository.logout()
    }

    fun isGuestUser(): Boolean = repository.isGuestUser()
}

class SettingViewModelFactory(private val repository: SettingRepo) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

