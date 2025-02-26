package com.example.foodplannerapplication.modules.home.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.home.model.server.models.FilteredMealModel
import com.example.foodplannerapplication.modules.home.model.server.services.RetrofitHelper
import com.example.foodplannerapplication.modules.home.view.adapters.CategoryAdapter
import com.example.foodplannerapplication.modules.home.view.adapters.FilteredMealsByCategoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FilteredMealsByCategoryFragment : Fragment() {
    private val filteredMealsByCategoryFragmentArgs: FilteredMealsByCategoryFragmentArgs by navArgs()

    // ui components
    private lateinit var filteredMealsByCategoryAdapter: FilteredMealsByCategoryAdapter
    private lateinit var rvFilteredMealsByCategory: RecyclerView
    private lateinit var filteredMeals: List<FilteredMealModel?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filtered_meals_by_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        fetchAllMealsFilteredByCategory(filteredMealsByCategoryFragmentArgs.categoryName)
    }

    // =============== fetchCategoriesFromApi &  setupRecyclerView =================================
    private fun setupRecyclerView(view: View) {
        rvFilteredMealsByCategory = view.findViewById(R.id.rv_filteredMealsByCategory)
        filteredMealsByCategoryAdapter = FilteredMealsByCategoryAdapter(null, requireContext()
        ) { meal -> openMealsDetailsActivity(meal) }

        rvFilteredMealsByCategory.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = filteredMealsByCategoryAdapter
        }
    }

    private fun openMealsDetailsActivity(mealId: String?) {
        val actionFilteredMealsByCategoryFragmentToMealDatailsFragment =
            FilteredMealsByCategoryFragmentDirections.actionFilteredMealsByCategoryFragmentToMealDatailsFragment(
                mealId
            )
        findNavController().navigate(actionFilteredMealsByCategoryFragmentToMealDatailsFragment)
    }

    // =============== fetchRandomMealFromApi & loadMealImage Using Glide ==========================
    private fun fetchAllMealsFilteredByCategory(categoryName: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getMealsByCategory(categoryName)
                val filteredMeals = response.meals.orEmpty()
                withContext(Dispatchers.Main) {
                    filteredMealsByCategoryAdapter.updateFilteredMeals(filteredMeals)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("===>", "Error Fetching Meal of the Day", e)
                }
            }
        }
    }

}