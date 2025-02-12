package com.example.foodplannerapplication.modules.auth.data.models

import com.example.foodplannerapplication.modules.auth.domain.entities.UserEntity
import com.google.firebase.auth.FirebaseUser

data class UserModel(
    override val name: String = "",
    override val email: String = "",
    override val phone: String = "",
    override val uId: String = ""
) : UserEntity(name, email, phone, uId) {

    companion object {
        fun fromFirebaseUser(user: FirebaseUser): UserModel {
            return UserModel(
                name = user.displayName ?: "",
                email = user.email ?: "",
                phone = user.phoneNumber ?: "",
                uId = user.uid
            )
        }

        fun fromJson(json: Map<String, Any?>): UserModel {
            return UserModel(
                name = json["name"] as? String ?: "",
                email = json["email"] as? String ?: "",
                phone = json["phone"] as? String ?: "",
                uId = json["uId"] as? String ?: ""
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
            "uId" to uId
        )
    }
}

