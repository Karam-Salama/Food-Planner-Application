package com.example.foodplannerapplication.modules.search.view

interface ICommonSearchFilteredListener {
    fun openMealsActivityByCategory(category: String?)
    fun openMealsActivityByArea(area: String?)
    fun onFilteredMealsClick(mealId: String?)
}