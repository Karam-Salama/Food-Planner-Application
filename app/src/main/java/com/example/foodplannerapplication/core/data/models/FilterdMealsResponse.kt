package com.example.foodplannerapplication.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class FilterdMealsResponse(
    val meals: List<FilteredMealModel?>?
)

@Entity(tableName = "Favorites_table")
data class FilteredMealModel(
    @PrimaryKey(autoGenerate = false)
    val idMeal: String = "",
    val strMeal: String? = null,
    val strMealThumb: String? = null,
    var isFavorite: Boolean = false
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "idMeal" to idMeal,
            "strMeal" to strMeal,
            "strMealThumb" to strMealThumb,
            "isFavorite" to isFavorite
        )
    }
}