package com.example.foodplannerapplication.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class FilterdMealsResponse(
    val meals: List<FilteredMealModel?>?
)

@Entity(tableName = "Favorites_table")
data class FilteredMealModel(
    @PrimaryKey(autoGenerate = false)
    val idMeal: String,
    val strMeal: String?,
    val strMealThumb: String?,
    var isFavorite: Boolean = false
)