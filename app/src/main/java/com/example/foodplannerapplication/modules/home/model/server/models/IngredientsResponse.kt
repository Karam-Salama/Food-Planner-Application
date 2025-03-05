package com.example.foodplannerapplication.modules.home.model.server.models

data class IngredientsResponse(
    val meals: List<IngredientModel?>?
)

data class IngredientModel(
    val idIngredient: String?,
    val strDescription: String?,
    val strIngredient: String?,
    val strType: String?
)