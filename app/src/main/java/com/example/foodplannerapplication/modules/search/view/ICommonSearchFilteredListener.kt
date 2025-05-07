package com.example.foodplannerapplication.modules.search.view

interface ICommonSearchFilteredListener {
    fun openMealsActivityByCategory(category: String?)
    fun openMealsActivityByArea(area: String?)
    fun openMealsActivityByIngredient(ingredient: String?)
    fun onFilteredMealsClick(mealId: String?)
}