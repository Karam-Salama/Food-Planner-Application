package com.example.foodplannerapplication.modules.favorite.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.core.data.models.FilteredMealModel
import com.example.foodplannerapplication.modules.favorite.models.FavoritesDao
import com.example.foodplannerapplication.modules.home.model.MealModel
import com.example.foodplannerapplication.core.data.server.retrofit.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddToFavoriteViewModel(private val dao: FavoritesDao, var retrofitHelper: RetrofitHelper) : ViewModel() {
    private var _filteredMealsList: MutableLiveData<List<FilteredMealModel>> = MutableLiveData()
    var filteredMealsList: LiveData<List<FilteredMealModel>> = _filteredMealsList

    private val _mealDetails = MutableLiveData<MealModel?>()
    val mealDetails: LiveData<MealModel?> = _mealDetails

    private var _message: MutableLiveData<String> = MutableLiveData()
    var message : LiveData<String> = _message

    suspend fun getFilteredMealsByCategory(categoryName: String?) {
        try {
            val response = RetrofitHelper.retrofitService.getMealsByCategory(categoryName)
            val filteredMeals = response.meals?.filterNotNull()?.mapNotNull {
                it.idMeal?.let { id -> it.copy(idMeal = id) }
            } ?: emptyList()

            viewModelScope.launch {
                if (filteredMeals.isEmpty()) {
                    _message.postValue("Couldn't Fetch Filtered Meals")
                } else {
                    _filteredMealsList.postValue(filteredMeals)
                }
            }
        } catch (e: Exception) {
            _message.postValue("Error Fetching Filtered Meals")
        }
    }

    suspend fun getFilteredMealsByIngredient(ingredientName: String?) {
        try {
            val response = RetrofitHelper.retrofitService.getMealsByIngredient(ingredientName)
            val filteredMeals = response.meals?.filterNotNull()?.mapNotNull {
                it.idMeal?.let { id -> it.copy(idMeal = id) }
            } ?: emptyList()

            viewModelScope.launch {
                if (filteredMeals.isEmpty()) {
                    _message.postValue("Couldn't Fetch Filtered Meals")
                } else {
                    _filteredMealsList.postValue(filteredMeals)
                }
            }
        } catch (e: Exception) {
            _message.postValue("Error Fetching Filtered Meals")
        }
    }

    suspend fun getFilteredMealsByArea(areaName: String?) {
        try {
            val response = RetrofitHelper.retrofitService.getMealsByArea(areaName)
            val filteredMeals = response.meals?.filterNotNull()?.mapNotNull {
                it.idMeal?.let { id -> it.copy(idMeal = id) }
            } ?: emptyList()

            viewModelScope.launch {
                if (filteredMeals.isEmpty()) {
                    _message.postValue("Couldn't Fetch Filtered Meals")
                } else {
                    _filteredMealsList.postValue(filteredMeals)
                }
            }
        } catch (e: Exception) {
            _message.postValue("Error Fetching Filtered Meals")
        }
    }

    suspend fun fetchMealDetailsById(mealId: String?) {
        if (mealId.isNullOrEmpty()) {
            _message.postValue("Invalid Meal ID")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = retrofitHelper.retrofitService.getMealById(mealId)
                val meal = response.meals?.firstOrNull()

                withContext(Dispatchers.Main) {
                    if (meal != null) {
                        _mealDetails.value = meal
                    } else {
                        _message.value = "Meal not found"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _message.value = "Error fetching meal details"
                    Log.e("===>", "Error Fetching Meal Details", e)
                }
            }
        }
    }

    suspend fun saveFilteredMeals(filteredMealModel: FilteredMealModel?) {
        if (filteredMealModel == null || filteredMealModel.idMeal.isEmpty()) {
            _message.postValue("Invalid Meal Data")
            return
        }

        try {
            val addingResult = dao.addFilteredMealsToFavorites(filteredMealModel)
            if (addingResult > 0) {
                _message.postValue("Added to Favorites")
            } else {
                _message.postValue("Already in Favorites")
            }
        } catch (e: Exception) {
            _message.postValue("Error Saving Meal")
            Log.e("===>", "Error Saving Meal", e)
        }
    }

    suspend fun fetchFavorites() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val list =  dao.getAllFavorites()
                if (list.isNullOrEmpty()) {
                    _filteredMealsList.postValue(emptyList())
                } else {
                    withContext(Dispatchers.Main) {
                        _filteredMealsList.postValue(list)
                    }
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("===>", "Error Fetching Favorites", e)
            }
        }
    }

    suspend fun removeFilteredMeals(filteredMealModel: FilteredMealModel?) {
        if (filteredMealModel == null || filteredMealModel.idMeal.isEmpty()) {
            _message.postValue("Invalid Meal Data")
            return
        }
        try {
            viewModelScope.launch (Dispatchers.IO){
                val result = dao.removeFilteredMealsFromFavorites(filteredMealModel)
                withContext(Dispatchers.Main){
                    if(result >0){
                        _message.postValue("Removed Successfully")
                    }else{
                        _message.postValue("Couldn't Remove")
                    }
                }
                fetchFavorites()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("===>", "Error Removing Filtered Meal", e)
            }
        }
    }
}

class MyFactory(private val dao: FavoritesDao, var retrofitHelper: RetrofitHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddToFavoriteViewModel(dao, retrofitHelper) as T
    }
}