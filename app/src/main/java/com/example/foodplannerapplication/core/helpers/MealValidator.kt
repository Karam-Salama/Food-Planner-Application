import com.example.foodplannerapplication.core.helpers.DateTimeValidator
import com.example.foodplannerapplication.modules.plans.models.AddMealModel

object MealValidator {

    fun validate(meal: AddMealModel?): Pair<Boolean, String?> {
        if (meal == null) return Pair(false, "Meal cannot be null")

        return when {
            meal.nameMealPlan.isBlank() -> Pair(false, "Meal name cannot be empty")
            meal.categoryMealPlan.isBlank() -> Pair(false, "Category cannot be empty")
            meal.thumbMealPlan.isNullOrBlank() -> Pair(false, "Image is required")
            meal.dateMealPlan == null -> Pair(false, "Date/time cannot be null")
            else -> {
                val (isValid, message) = DateTimeValidator.validateDateTimeInFuture(meal.dateMealPlan)
                if (!isValid) Pair(false, message)
                else Pair(true, null)
            }
        }
    }
}
