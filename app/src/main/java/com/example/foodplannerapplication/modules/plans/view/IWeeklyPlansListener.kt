package com.example.foodplannerapplication.modules.plans.view
import com.example.foodplannerapplication.modules.plans.models.AddMealModel

interface  IWeeklyPlansListener{
    fun onDeleteWeeklyPlansClick(addMealModel: AddMealModel)
}