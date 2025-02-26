package com.example.foodplannerapplication.core.utils.functions

object AppRegex {
    fun isEmailValid(email: String): Boolean {
        return Regex("""^.+@[a-zA-Z]+\.[a-zA-Z]+(\.{0,1}[a-zA-Z]+)$""").matches(email)
    }

    fun isPasswordValid(password: String): Boolean {
        return Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")
            .matches(password)
    }

    fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return Regex("""^(010|011|012|015)[0-9]{8}$""").matches(phoneNumber)
    }

    fun hasLowerCase(password: String): Boolean {
        return Regex("""^(?=.*[a-z])""").containsMatchIn(password)
    }

    fun hasUpperCase(password: String): Boolean {
        return Regex("""^(?=.*[A-Z])""").containsMatchIn(password)
    }

    fun hasNumber(password: String): Boolean {
        return Regex("""^(?=.*?[0-9])""").containsMatchIn(password)
    }

    fun hasSpecialCharacter(password: String): Boolean {
        return Regex("""^(?=.*?[#?!@$%^&*-])""").containsMatchIn(password)
    }

    fun passHasMinLength(password: String): Boolean {
        return password.length >= 8
    }

    fun nameHasMinLength(name: String): Boolean {
        return name.length >= 3
    }
}
