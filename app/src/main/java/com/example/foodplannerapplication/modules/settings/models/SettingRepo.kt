package com.example.foodplannerapplication.modules.settings.models
import com.example.foodplannerapplication.core.data.cache.CacheHelper
import com.example.foodplannerapplication.core.utils.Constants
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class SettingRepo {
    fun isGuestUser(): Boolean = CacheHelper.getBoolean(Constants.OnBording_SKIP_KEY, false)

    fun getCurrentUser(): FirebaseUser? = Firebase.auth.currentUser

    suspend fun updateUserName(newName: String): Boolean {
        return try {
            val user = Firebase.auth.currentUser
            val profileUpdates = userProfileChangeRequest {
                displayName = newName
            }
            user?.updateProfile(profileUpdates)?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUserEmail(newEmail: String): Boolean {
        return try {
            Firebase.auth.currentUser?.updateEmail(newEmail)?.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean {
        return try {
            val user = Firebase.auth.currentUser ?: return false
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun logout() {
        Firebase.auth.signOut()
    }
}