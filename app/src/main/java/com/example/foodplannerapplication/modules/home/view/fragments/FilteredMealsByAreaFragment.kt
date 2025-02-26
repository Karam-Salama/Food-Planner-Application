package com.example.foodplannerapplication.modules.home.view.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.model.FilteredMealModel
import com.example.foodplannerapplication.core.model.ICommonFilteredMealListener
import com.example.foodplannerapplication.core.model.cache.room.database.FavoritesDatabase
import com.example.foodplannerapplication.core.viewmodel.AddToFavoriteViewModel
import com.example.foodplannerapplication.core.viewmodel.MyFactory
import com.example.foodplannerapplication.modules.home.model.server.services.RetrofitHelper
import com.example.foodplannerapplication.modules.home.view.adapters.FilteredMealsByAreaAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FilteredMealsByAreaFragment : Fragment(), ICommonFilteredMealListener {
    // arguments
    private val FilteredMealsByAreaFragmentArgs: FilteredMealsByAreaFragmentArgs by navArgs()

    // view model
    private lateinit var addToFavoriteViewModel: AddToFavoriteViewModel

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

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setUpViewModel()
        extractedDataFromViewModel()
        observeViewModel()
    }

    private fun setupRecyclerView(view: View) {
        rvFilteredMealsByArea = view.findViewById(R.id.rv_filteredMealsByArea)
        filteredMealsByAreaAdapter = FilteredMealsByAreaAdapter(null, requireContext(), this)

        rvFilteredMealsByArea.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = filteredMealsByAreaAdapter
        }
    }

    private fun setUpViewModel() {
        var dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        var myFactory = MyFactory(dao, RetrofitHelper)
        addToFavoriteViewModel = ViewModelProvider(this, myFactory).get(AddToFavoriteViewModel::class.java)
    }

    private fun extractedDataFromViewModel() {
        lifecycleScope.launch {
            addToFavoriteViewModel.getFilteredMealsByArea(FilteredMealsByAreaFragmentArgs.areaName)
        }
    }

    private fun observeViewModel() {
        addToFavoriteViewModel.filteredMealsList.observe(viewLifecycleOwner) { newList ->
            filteredMealsByAreaAdapter.filteredMeals = newList.toList()
            filteredMealsByAreaAdapter.notifyDataSetChanged()
        }


        addToFavoriteViewModel.message.observe(viewLifecycleOwner) {
            Snackbar.make(rvFilteredMealsByArea, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onFilteredMealsFavoriteClick(filteredMealsModel: FilteredMealModel?) {
        lifecycleScope.launch {
            addToFavoriteViewModel.saveFilteredMeals(filteredMealsModel)
        }
    }

    override fun onFilteredMealsClick(mealId: String?) {
        val actionFilteredMealsByAreaFragmentToMealDatailsFragment =
            FilteredMealsByAreaFragmentDirections.actionFilteredMealsByAreaFragmentToMealDatailsFragment(
                mealId
            )
        findNavController().navigate(actionFilteredMealsByAreaFragmentToMealDatailsFragment)
    }
}