package com.example.foodplannerapplication.core.data.models

interface ICommonFilteredMealListener {
    fun onFilteredMealsFavoriteClick(filteredMealsModel: FilteredMealModel?)
    fun onFilteredMealsClick(mealId: String?)
}