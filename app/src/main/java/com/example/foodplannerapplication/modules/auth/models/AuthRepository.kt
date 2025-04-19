package com.example.foodplannerapplication.modules.auth.models

import com.facebook.AccessToken
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth

    suspend fun registerWithEmail(email: String, password: String, fullName: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val profileUpdates = userProfileChangeRequest {
                displayName = fullName
            }
            authResult.user?.updateProfile(profileUpdates)?.await()
            authResult.user?.sendEmailVerification()?.await()
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            if (authResult.user?.isEmailVerified == false) {
                auth.signOut()
                throw Exception("Email not verified")
            }
            Result.success(authResult.user ?: throw Exception("Login failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            if (authResult.user?.isEmailVerified == false) {
                auth.signOut()
                throw Exception("Email not verified")
            }
            Result.success(authResult.user ?: throw Exception("Google auth failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginWithFacebook(token: AccessToken): Result<FirebaseUser> {
        return try {
            val credential = FacebookAuthProvider.getCredential(token.token)
            val authResult = auth.signInWithCredential(credential).await()
            Result.success(authResult.user ?: throw Exception("Facebook auth failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            Firebase.auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidUserException -> {
                    if (e.errorCode == "ERROR_USER_NOT_FOUND") {
                        "No account found with this email"
                    } else {
                        "Invalid email address"
                    }
                }
                else -> "Failed to send reset email: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        }
    }
}