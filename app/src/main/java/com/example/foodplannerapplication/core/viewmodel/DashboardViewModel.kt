package com.example.foodplannerapplication.core.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.modules.home.model.server.models.AreaModel
import com.example.foodplannerapplication.modules.home.model.server.models.CategoryModel
import com.example.foodplannerapplication.modules.home.model.server.services.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class DashboardViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<CategoryModel>>()
    val categories: LiveData<List<CategoryModel>> get() = _categories

    private val _areas = MutableLiveData<List<AreaModel>>()
    val areas: LiveData<List<AreaModel>> get() = _areas


    init {
        fetchCategories()
        fetchAreas()
    }

    private fun fetchCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getCategories()
                _categories.postValue(response.categories.orEmpty().filterNotNull())
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error Fetching Categories", e)
            }
        }
    }

    private fun fetchAreas() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getAreas()
                _areas.postValue(response.meals.orEmpty().filterNotNull())
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error Fetching Areas", e)
            }
        }
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
