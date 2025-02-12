package com.example.foodplannerapplication.modules.auth.data.repos
import com.example.foodplannerapplication.modules.auth.domain.entities.UserEntity
import com.google.android.gms.tasks.Task
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.Flow

abstract class AuthRepo {
    abstract fun createUserWithEmailAndPassword(email: String, password: String, name: String, phone: String): Result<UserEntity>

    abstract fun signInWithEmailAndPassword(email: String, password: String): Result<UserEntity>

    abstract fun signInWithGoogle(account: GoogleSignInAccount): Result<UserEntity>

    abstract fun forgetPassword(email: String): Task<Unit>

    abstract fun verifyEmail(): Task<Unit>

    abstract fun addUserData(user: UserEntity): Task<Void>

    abstract fun saveUserData(user: UserEntity): Task<Void>

    abstract fun getUserData(uid: String): Flow<UserEntity>

    abstract fun signOut(): Task<Unit>
}
