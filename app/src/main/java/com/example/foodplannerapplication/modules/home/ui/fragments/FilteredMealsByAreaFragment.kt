package com.example.foodplannerapplication.modules.home.ui.fragments

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
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.home.data.server.models.FilteredMealModel
import com.example.foodplannerapplication.modules.home.data.server.services.RetrofitHelper
import com.example.foodplannerapplication.modules.home.ui.adapters.FilteredMealsByAreaAdapter
import com.example.foodplannerapplication.modules.home.ui.adapters.FilteredMealsByCategoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FilteredMealsByAreaFragment : Fragment() {
    private val FilteredMealsByAreaFragmentArgs: FilteredMealsByAreaFragmentArgs by navArgs()

    // ui components
    private lateinit var filteredMealsByAreaAdapter: FilteredMealsByAreaAdapter
    private lateinit var rvFilteredMealsByArea: RecyclerView
    private lateinit var filteredMeals: List<FilteredMealModel?>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filtered_meals_by_area, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        fetchAllMealsFilteredByArea(FilteredMealsByAreaFragmentArgs.areaName)
    }

    // =============== fetchCategoriesFromApi &  setupRecyclerView =================================
    private fun setupRecyclerView(view: View) {
        rvFilteredMealsByArea = view.findViewById(R.id.rv_filteredMealsByArea)
        filteredMealsByAreaAdapter = FilteredMealsByAreaAdapter(null, requireContext()) { meal ->
            openMealsDetailsActivity(meal)
        }

        rvFilteredMealsByArea.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = filteredMealsByAreaAdapter
        }
    }

    private fun openMealsDetailsActivity(mealId: String?) {
        val actionFilteredMealsByAreaFragmentToMealDatailsFragment =
            FilteredMealsByAreaFragmentDirections.actionFilteredMealsByAreaFragmentToMealDatailsFragment(
                mealId
            )
        findNavController().navigate(actionFilteredMealsByAreaFragmentToMealDatailsFragment)
    }

    // =============== fetchRandomMealFromApi & loadMealImage Using Glide ==========================
    private fun fetchAllMealsFilteredByArea(areaName: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getMealsByArea(areaName)
                val filteredMeals = response.meals.orEmpty()
                withContext(Dispatchers.Main) {
                    filteredMealsByAreaAdapter.updateFilteredMeals(filteredMeals)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("===>", "Error Fetching Meal of the Day", e)
                }
            }
        }
    }
}