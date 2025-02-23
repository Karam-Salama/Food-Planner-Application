package com.example.foodplannerapplication.modules.home.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.home.data.server.services.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MealDatailsFragment : Fragment() {
    private val MealDatailsFragmentArgs: MealDatailsFragmentArgs by navArgs()
    private lateinit var mealTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal_datails, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        fetchMealDetailsByIdFromApi(MealDatailsFragmentArgs.mealId)
    }

    private fun initViews(view: View) {
        mealTitle = view.findViewById(R.id.tv_mealName)
    }

    private fun fetchMealDetailsByIdFromApi(mealId: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getMealById(mealId)
                val filteredMeals = response.meals?.firstOrNull()
                withContext(Dispatchers.Main) {
                    mealTitle.text = filteredMeals?.strMeal
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("===>", "Error Fetching Meal of the Day", e)
                }
            }
        }
    }

}