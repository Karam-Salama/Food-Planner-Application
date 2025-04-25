package com.example.foodplannerapplication.modules.home.data.model

data class IngredientsResponse(
    val meals: List<IngredientModel?>?
)

data class IngredientModel(
    val idIngredient: String?,
    val strDescription: String?,
    val strIngredient: String?,
    val strType: String?
)