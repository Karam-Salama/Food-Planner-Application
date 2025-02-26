package com.example.foodplannerapplication.modules.home.model.server.models

data class AreaResponse(
    val meals: List<AreaModel?>?
)

data class AreaModel(
    val strArea: String?
)