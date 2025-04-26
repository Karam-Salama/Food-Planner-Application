package com.example.foodplannerapplication.modules.favorite.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.core.data.models.FilteredMealModel
import com.example.foodplannerapplication.modules.favorite.models.FavoritesDao
import com.example.foodplannerapplication.modules.home.data.model.MealModel
import com.example.foodplannerapplication.core.data.server.retrofit.RetrofitHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AddMealToFavoritesViewModel(private val dao: FavoritesDao, var retrofitHelper: RetrofitHelper) : ViewModel() {
    private var _filteredMealsList: MutableLiveData<List<FilteredMealModel>> = MutableLiveData()
    var filteredMealsList: LiveData<List<FilteredMealModel>> = _filteredMealsList

    private val _mealDetails = MutableLiveData<MealModel?>()
    val mealDetails: LiveData<MealModel?> = _mealDetails

    private var _message: MutableLiveData<String> = MutableLiveData()
    var message : LiveData<String> = _message

    private val firestore = Firebase.firestore
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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
                syncWithFirebase(filteredMealModel, true)
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
            val result = dao.removeFilteredMealsFromFavorites(filteredMealModel)
            if (result > 0) {
                if (currentUserId.isNotEmpty()) {
                    firestore.collection("users").document(currentUserId)
                        .collection("favorites").document(filteredMealModel.idMeal)
                        .delete()
                        .addOnSuccessListener {
                            _message.postValue("Removed successfully")
                        }
                        .addOnFailureListener { e ->
                            _message.postValue("Failed to sync with cloud")
                            viewModelScope.launch {
                                dao.addFilteredMealsToFavorites(filteredMealModel)
                            }
                        }
                }

                fetchFavorites()
            } else {
                _message.postValue("Meal not found in favorites")
            }
        } catch (e: Exception) {
            _message.postValue("Error removing meal: ${e.message}")
            Log.e("Firestore", "Remove error", e)
        }
    }

    private suspend fun syncWithFirebase(meal: FilteredMealModel, isAdding: Boolean) {
        try {
            val favRef = firestore.collection("users").document(currentUserId)
                .collection("favorites").document(meal.idMeal)

            if (isAdding) {
                favRef.set(meal.toMap()).await()
            } else {
                favRef.delete().await()
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error syncing favorite", e)
        }
    }

    fun listenForFirebaseFavorites() {
        if (currentUserId.isEmpty()) return

        firestore.collection("users").document(currentUserId)
            .collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Listen failed", error)
                    return@addSnapshotListener
                }

                val firebaseFavorites = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FilteredMealModel::class.java)
                } ?: emptyList()

                viewModelScope.launch(Dispatchers.IO) {
                    syncLocalWithFirebase(firebaseFavorites)
                }
            }
    }

    private suspend fun syncLocalWithFirebase(firebaseFavorites: List<FilteredMealModel>) {
        val localFavorites = dao.getAllFavorites()
        val toAdd = firebaseFavorites.filter { fbMeal ->
            localFavorites.none { it.idMeal == fbMeal.idMeal }
        }
        toAdd.forEach { dao.addFilteredMealsToFavorites(it) }

        val toRemove = localFavorites.filter { localMeal ->
            firebaseFavorites.none { it.idMeal == localMeal.idMeal }
        }
        toRemove.forEach { dao.removeFilteredMealsFromFavorites(it) }
        fetchFavorites()
    }
}

class AddMealToFavoritesViewModelFactory(private val dao: FavoritesDao, var retrofitHelper: RetrofitHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddMealToFavoritesViewModel(dao, retrofitHelper) as T
    }
}