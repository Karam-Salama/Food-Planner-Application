package com.example.foodplannerapplication.modules.auth.domain.entities

open class UserEntity(
    open val name: String,
    open val email: String,
    open val phone: String,
    open val uId: String
)