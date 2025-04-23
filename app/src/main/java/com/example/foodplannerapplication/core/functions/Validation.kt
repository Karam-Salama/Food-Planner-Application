package com.example.foodplannerapplication.core.functions

import android.util.Patterns

object Validation {

    fun validateName(name: String): String? {
        return when {
            name.isEmpty() -> "Full name is required."
            name.length < 3 -> "Full name must be at least 3 characters."
            !name.matches(Regex("^[a-zA-Z\\p{L}\\s]+$")) -> "Full name can only contain letters and spaces."
            else -> null
        }
    }

    fun validatePhone(phone: String): String? {
        return when {
            phone.isEmpty() -> "Phone number is required."
            !phone.matches(Regex("^(010|011|012|015)\\d{8}$")) -> "Invalid phone number format."
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> "Email is required."
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Invalid email format."
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required."
            password.length < 8 -> "Password must be at least 8 characters long."
            !password.contains(Regex("[A-Z]")) -> "Password must contain at least one uppercase letter."
            !password.contains(Regex("[a-z]")) -> "Password must contain at least one lowercase letter."
            !password.contains(Regex("\\d")) -> "Password must contain at least one digit."
            !password.contains(Regex("[!@#\$%^&*()]")) -> "Password must contain at least one special character (!@#\$%^&*())."
            else -> null
        }
    }

    fun validateConfirmPassword(confirmPassword: String?, password: String?): String? {
        return when {
            confirmPassword.isNullOrEmpty() -> "This Field is required."
            password.isNullOrEmpty() -> "Password Field is required."
            confirmPassword != password -> "Passwords don't match."
            else -> null
        }
    }
}
