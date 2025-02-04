package com.example.foodplannerapplication.core.services

import android.content.Context
import com.example.foodplannerapplication.core.errors.CustomException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import android.util.Log
import com.example.foodplannerapplication.R
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(private val context: Context) {

    suspend fun createUserWithEmailAndPassword(email: String, password: String): FirebaseUser {
        return try {
            val credential = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            credential.user!!
        } catch (e: FirebaseAuthException) {
            Log.e("FirebaseAuthService", "Exception in createUserWithEmailAndPassword method: ${e.message}")
            when (e.errorCode) {
                "weak-password" -> throw CustomException(context.getString(R.string.weak_password_error))
                "email-already-in-use" -> throw CustomException(context.getString(R.string.email_already_in_use_error))
                "invalid-email" -> throw CustomException(context.getString(R.string.invalid_email_error))
                "operation-not-allowed" -> throw CustomException(context.getString(R.string.operation_not_allowed_error))
                "network-request-failed" -> throw CustomException(context.getString(R.string.network_error))
                else -> throw CustomException(context.getString(R.string.generic_error))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Exception in createUserWithEmailAndPassword method: ${e.message}")
            throw CustomException(context.getString(R.string.generic_error))
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseUser {
        return try {
            val credential = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            credential.user!!
        } catch (e: FirebaseAuthException) {
            Log.e("FirebaseAuthService", "Exception in signInWithEmailAndPassword method: ${e.message}")
            when (e.errorCode) {
                "user-not-found", "invalid-credential" -> throw CustomException(context.getString(R.string.invalid_credentials_error))
                "wrong-password" -> throw CustomException(context.getString(R.string.invalid_credentials_error))
                "invalid-email" -> throw CustomException(context.getString(R.string.invalid_email_error))
                "user-disabled" -> throw CustomException(context.getString(R.string.user_disabled_error))
                "operation-not-allowed" -> throw CustomException(context.getString(R.string.operation_not_allowed_error))
                "network-request-failed" -> throw CustomException(context.getString(R.string.network_error))
                else -> throw CustomException(context.getString(R.string.generic_error))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Exception in signInWithEmailAndPassword method: ${e.message}")
            throw CustomException(context.getString(R.string.generic_error))
        }
    }

    suspend fun forgetPassword(email: String) {
        try {
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
        } catch (e: FirebaseAuthException) {
            Log.e("FirebaseAuthService", "Exception in forgetPassword method: ${e.message}")
            if (e.message == "There is no user record corresponding to this identifier. The user may have been deleted.") {
                throw CustomException(context.getString(R.string.no_user_record_error))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Exception in forgetPassword method: ${e.message}")
            throw CustomException(context.getString(R.string.generic_error))
        }
    }

    suspend fun signOut() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Exception in signOut method: ${e.message}")
            throw CustomException(context.getString(R.string.generic_error))
        }
    }

    suspend fun deleteUser() {
        try {
            FirebaseAuth.getInstance().currentUser?.delete()?.await()
        } catch (e: FirebaseAuthException) {
            Log.e("FirebaseAuthService", "Exception in deleteUser method: ${e.message}")
            throw CustomException(context.getString(R.string.generic_error))
        }
    }
}
