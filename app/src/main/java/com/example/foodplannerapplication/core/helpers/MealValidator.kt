package com.example.foodplannerapplication.core.helpers

import com.example.foodplannerapplication.modules.plans.models.AddMealModel

object MealValidator {
    fun isValid(meal: AddMealModel): Boolean {
        return meal.nameMealPlan.isNotEmpty() &&
                meal.dateMealPlan != 0L
    }
}
