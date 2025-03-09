package com.example.foodplannerapplication.core.utils.helpers

import com.example.foodplannerapplication.modules.home.model.cache.entity.AddMealModel

object MealValidator {
    fun isValid(meal: AddMealModel): Boolean {
        return meal.nameMealPlan.isNotEmpty() &&
                meal.dateMealPlan != 0L
    }
}
