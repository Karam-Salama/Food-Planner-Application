package com.example.foodplannerapplication.core.utils.errors

sealed class Failure(message: String?) : Throwable(message)

class ServerFailure(message: String) : Failure(message)