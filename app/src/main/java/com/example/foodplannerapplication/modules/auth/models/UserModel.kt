package com.example.foodplannerapplication.modules.auth.models

// ملف جديد: UserModel.kt
data class UserModel(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String? = null,
    val provider: String = "email" // أو "google" أو "facebook"
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "name" to name,
            "email" to email,
            "phone" to phone,
            "provider" to provider
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any>): UserModel {
            return UserModel(
                uid = map["uid"] as? String ?: "",
                name = map["name"] as? String ?: "",
                email = map["email"] as? String ?: "",
                phone = map["phone"] as? String,
                provider = map["provider"] as? String ?: "email"
            )
        }
    }
}