package com.example.foodplannerapplication.modules.home.data.models

data class FilterdMealsResponse(
    val meals: List<FilteredMealModel?>?
)

data class FilteredMealModel(
    val idMeal: String?,
    val strMeal: String?,
    val strMealThumb: String?
)