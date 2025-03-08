package com.example.foodplannerapplication.core.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.core.model.FilteredMealModel
import com.example.foodplannerapplication.core.utils.functions.CountryFlagMapper
import com.example.foodplannerapplication.modules.home.model.server.models.AreaModel
import com.example.foodplannerapplication.modules.home.model.server.models.CategoryModel
import com.example.foodplannerapplication.modules.home.model.server.models.IngredientModel
import com.example.foodplannerapplication.modules.home.model.server.services.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class FilterType {
    COUNTRIES, CATEGORIES, INGREDIENTS
}

class DashboardViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<CategoryModel>>()
    val categories: LiveData<List<CategoryModel>> get() = _categories

    private val _areas = MutableLiveData<List<AreaModel>>()
    val areas: LiveData<List<AreaModel>> get() = _areas

    private val _ingredients = MutableLiveData<List<IngredientModel>>()
    val ingredients: LiveData<List<IngredientModel>> get() = _ingredients

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> get() = _searchQuery

    private val _filteredData = MutableLiveData<List<FilteredMealModel>>()
    val filteredData: LiveData<List<FilteredMealModel>> get() = _filteredData


    // 🔹 تحديد الفلتر الحالي (Countries, Categories, Ingredients)
    private var selectedFilterType: FilterType = FilterType.CATEGORIES

    init {
        fetchCategories()
        fetchAreas()
        fetchIngredients()
    }

    private fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getCategories()
                _categories.postValue(response.categories.orEmpty().filterNotNull())
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error Fetching Categories", e)
            }
        }
    }

    private fun fetchAreas() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getAreas()
                _areas.postValue(response.meals.orEmpty().filterNotNull())
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error Fetching Areas", e)
            }
        }
    }

    private fun fetchIngredients() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getIngredients()
                _ingredients.postValue(response.meals.orEmpty().filterNotNull())
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error Fetching Ingredients", e)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterData()
    }

    fun setFilterType(filterType: FilterType) {
        selectedFilterType = filterType
        filterData()
    }

    private fun filterData() {
        val query = _searchQuery.value.orEmpty().lowercase()

        val filteredList: List<FilteredMealModel> = when (selectedFilterType) {
            FilterType.COUNTRIES -> _areas.value?.filter { it.strArea?.lowercase()?.contains(query) == true }
                ?.map { area ->
                    FilteredMealModel(
                        idMeal = "",
                        strMeal = area.strArea ?: "",
                        strMealThumb = CountryFlagMapper.getFlagUrl(area.strArea ?: ""),
                        isFavorite = false
                    )
                }

            FilterType.CATEGORIES -> _categories.value?.filter { it.strCategory?.lowercase()?.contains(query) == true }
                ?.map { category ->
                    FilteredMealModel(
                        idMeal = "",
                        strMeal = category.strCategory ?: "",
                        strMealThumb = category.strCategoryThumb ?: "",
                        isFavorite = false
                    )
                }

            FilterType.INGREDIENTS -> _ingredients.value?.filter { it.strIngredient?.lowercase()?.contains(query) == true }
                ?.map { ingredient ->
                    FilteredMealModel(
                        idMeal = "",
                        strMeal = ingredient.strIngredient ?: "",
                        strMealThumb = "https://www.themealdb.com/images/ingredients/${ingredient.strIngredient?.replace(" ", "%20")}-Small.png",
                        isFavorite = false
                    )
                }

            else -> emptyList()
        } ?: emptyList()

        _filteredData.postValue(filteredList)
    }


}

class MyHomeFactory() : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
