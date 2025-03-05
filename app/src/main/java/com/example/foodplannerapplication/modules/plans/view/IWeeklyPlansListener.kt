package com.example.foodplannerapplication.modules.plans.view

import com.example.foodplannerapplication.modules.home.model.cache.entity.AddMealModel

interface  IWeeklyPlansListener{
    fun onDeleteWeeklyPlansClick(addMealModel: AddMealModel)
}