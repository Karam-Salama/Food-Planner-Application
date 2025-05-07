package com.example.foodplannerapplication.modules.home.data.repo
import com.example.foodplannerapplication.core.data.cache.CacheHelper
import com.example.foodplannerapplication.core.data.server.retrofit.RetrofitHelper
import com.example.foodplannerapplication.core.utils.Constants
import com.example.foodplannerapplication.modules.home.data.model.AreaModel
import com.example.foodplannerapplication.modules.home.data.model.CategoryModel
import com.example.foodplannerapplication.modules.home.data.model.IngredientModel
import com.example.foodplannerapplication.modules.home.data.model.MealModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository {
    private val retrofitService = RetrofitHelper.retrofitService

    suspend fun getRandomMeal(): MealModel? = withContext(Dispatchers.IO) {
        try {
            retrofitService.getMealOfTheDay().meals?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCategories(): List<CategoryModel> = withContext(Dispatchers.IO) {
        try {
            retrofitService.getCategories().categories?.filterNotNull() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAreas(): List<AreaModel> = withContext(Dispatchers.IO) {
        try {
            retrofitService.getAreas().meals?.filterNotNull() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getIngredients(): List<IngredientModel> = withContext(Dispatchers.IO) {
        try {
            retrofitService.getIngredients().meals?.filterNotNull() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun isGuestUser(): Boolean = CacheHelper.getBoolean(Constants.OnBording_SKIP_KEY, false)

    fun isUserLoggedIn(): Boolean = Firebase.auth.currentUser != null
}