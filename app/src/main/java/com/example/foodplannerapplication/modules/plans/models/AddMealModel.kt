package com.example.foodplannerapplication.modules.plans.models
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Plans_table")
data class AddMealModel(
    @PrimaryKey(autoGenerate = true)
    val idMealPlan: Int = 0,
    val thumbMealPlan: String = "",
    val nameMealPlan: String = "",
    val categoryMealPlan: String = "",
    val dateMealPlan: Long? = null,
) {
   fun toMap(): Map<String, Any?> {
        return mapOf(
            "idMealPlan" to idMealPlan,
            "thumbMealPlan" to thumbMealPlan,
            "nameMealPlan" to nameMealPlan,
            "categoryMealPlan" to categoryMealPlan,
            "dateMealPlan" to dateMealPlan,
        )
    }
}
