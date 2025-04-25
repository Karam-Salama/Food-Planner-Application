package com.example.foodplannerapplication.modules.plans.models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AddMealToPlansDao {
    @Query("SELECT * FROM Plans_table")
    suspend fun getAllPlans(): List<AddMealModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlan(plan: AddMealModel): Long

    @Delete
    suspend fun removePlan(plan: AddMealModel): Int
}