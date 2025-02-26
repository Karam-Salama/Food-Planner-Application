package com.example.foodplannerapplication.modules.home.model.cache.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Plans_table")
data class AddMealModel(
    @PrimaryKey(autoGenerate = true)
    val idMealPlan: Int = 0,
    val thumbMealPlan: String,
    val nameMealPlan: String,
    val caloriesMealPlan: Int,
    val categoryMealPlan: String,
    val dateMealPlan: Long,
    val timeMealPlan: String,
    val descriptionMealPlan: String,
)
