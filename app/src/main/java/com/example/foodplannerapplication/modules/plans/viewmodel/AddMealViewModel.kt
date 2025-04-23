package com.example.foodplannerapplication.modules.plans.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.foodplannerapplication.modules.plans.models.dao.AddMealDao
import com.example.foodplannerapplication.modules.plans.models.entity.AddMealModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddMealViewModel(private val addMealDao: AddMealDao) : ViewModel() {

    private val _mealsPlanList = MutableLiveData<List<AddMealModel>>()
    val mealsPlanList: LiveData<List<AddMealModel>> = _mealsPlanList

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    init {
        fetchPlans()
    }

    fun fetchPlans() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = addMealDao.getAllPlans()
                _mealsPlanList.postValue(list)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error Fetching Plans", e)
            }
        }
    }

    fun addPlan(plan: AddMealModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = addMealDao.addPlan(plan)
                _message.postValue(if (result > 0) "Added to Plans" else "Already in Plans")
                fetchPlans()
            } catch (e: Exception) {
                _message.postValue("Error Saving Meal")
                Log.e("ViewModel", "Error Saving Meal", e)
            }
        }
    }

    fun removePlan(plan: AddMealModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = addMealDao.removePlan(plan)
                _message.postValue(if (result > 0) "Removed Successfully" else "Couldn't Remove")
                fetchPlans()
            } catch (e: Exception) {
                Log.e("ViewModel", "Error Removing Meal", e)
            }
        }
    }
}


class MyFactory(private val addMealDao: AddMealDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddMealViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddMealViewModel(addMealDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
