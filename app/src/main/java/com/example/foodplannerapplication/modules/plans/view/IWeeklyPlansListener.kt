package com.example.foodplannerapplication.modules.plans.view

import com.example.foodplannerapplication.modules.plans.models.entity.AddMealModel

interface  IWeeklyPlansListener{
    fun onDeleteWeeklyPlansClick(addMealModel: AddMealModel)
}