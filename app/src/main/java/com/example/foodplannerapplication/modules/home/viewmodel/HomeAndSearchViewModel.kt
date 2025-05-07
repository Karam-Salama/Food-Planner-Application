package com.example.foodplannerapplication.modules.home.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.modules.home.data.model.AreaModel
import com.example.foodplannerapplication.modules.home.data.model.CategoryModel
import com.example.foodplannerapplication.modules.home.data.model.IngredientModel
import com.example.foodplannerapplication.modules.home.data.model.MealModel
import com.example.foodplannerapplication.modules.home.data.repo.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

enum class FilterType {
    COUNTRIES, CATEGORIES, INGREDIENTS
}

class HomeAndSearchViewModel(private val repository: HomeRepository) : ViewModel() {
    private val _randomMeal = MutableLiveData<MealModel?>()
    val randomMeal: LiveData<MealModel?> get() = _randomMeal

    private val _categories = MutableLiveData<List<CategoryModel>>()
    val categories: LiveData<List<CategoryModel>> get() = _categories

    private val _areas = MutableLiveData<List<AreaModel>>()
    val areas: LiveData<List<AreaModel>> get() = _areas

    private val _ingredients = MutableLiveData<List<IngredientModel>>()
     val ingredients: LiveData<List<IngredientModel>> get() = _ingredients

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> get() = _searchQuery

    private val _filteredData = MutableLiveData<List<Any>>()
    val filteredData: LiveData<List<Any>> get() = _filteredData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    var selectedFilterType: FilterType = FilterType.COUNTRIES

    init {
        loadAllData()
    }

    fun loadAllData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val randomMealDeferred = async(Dispatchers.IO) { repository.getRandomMeal() }
                val categoriesDeferred = async(Dispatchers.IO) { repository.getCategories() }
                val areasDeferred = async(Dispatchers.IO) { repository.getAreas() }
                 val ingredientsDeferred = async(Dispatchers.IO) { repository.getIngredients() }

                _randomMeal.value = randomMealDeferred.await()
                _categories.value = categoriesDeferred.await()
                _areas.value = areasDeferred.await()
                _ingredients.value = ingredientsDeferred.await()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
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

        val filteredList: List<Any> = when (selectedFilterType) {
            FilterType.COUNTRIES -> _areas.value?.filter { it.strArea?.lowercase()?.contains(query) == true }
            FilterType.CATEGORIES -> _categories.value?.filter { it.strCategory?.lowercase()?.contains(query) == true }
            FilterType.INGREDIENTS -> _ingredients.value?.filter { it.strIngredient?.lowercase()?.contains(query) == true }
        } ?: emptyList()

        _filteredData.value = filteredList
    }

    fun isGuestUser(): Boolean = repository.isGuestUser()

    fun isUserLoggedIn(): Boolean = repository.isUserLoggedIn()
}