package com.example.foodplannerapplication.core.model

interface ICommonFilteredMealListener {
    fun onFilteredMealsFavoriteClick(filteredMealsModel: FilteredMealModel?)
    fun onFilteredMealsClick(mealId: String?)
}