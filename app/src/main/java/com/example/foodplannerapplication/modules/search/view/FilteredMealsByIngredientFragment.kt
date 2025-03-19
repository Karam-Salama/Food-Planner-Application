package com.example.foodplannerapplication.modules.search.view

import android.os.Bundle
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
import com.example.foodplannerapplication.modules.home.view.adapters.FilteredMealsByCategoryAdapter
import com.example.foodplannerapplication.modules.home.view.adapters.FilteredMealsByIngredientAdapter
import com.example.foodplannerapplication.modules.home.view.fragments.FilteredMealsByCategoryFragmentArgs
import com.example.foodplannerapplication.modules.home.view.fragments.FilteredMealsByCategoryFragmentDirections
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FilteredMealsByIngredientFragment : Fragment(), ICommonFilteredMealListener {
    // arguments
    private val filteredMealsByIngredientArgs: FilteredMealsByIngredientFragmentArgs by navArgs()

    // view model
    private lateinit var addToFavoriteViewModel: AddToFavoriteViewModel

    // ui components
    private lateinit var filteredMealsByIngredientAdapter: FilteredMealsByIngredientAdapter
    private lateinit var rvFilteredMealsByIngredient: RecyclerView
    private lateinit var filteredMeals: List<FilteredMealModel?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filtered_meals_by_ingredient, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        setUpViewModel()
        extractedDataFromViewModel()
        observeViewModel()
    }

    private fun setupRecyclerView(view: View) {
        rvFilteredMealsByIngredient = view.findViewById(R.id.rv_filteredMealsByIngredient)
        filteredMealsByIngredientAdapter = FilteredMealsByIngredientAdapter(null, requireContext(), this)

        rvFilteredMealsByIngredient.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = filteredMealsByIngredientAdapter
        }
    }

    private fun setUpViewModel() {
        var dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        var myFactory = MyFactory(dao, RetrofitHelper)
        addToFavoriteViewModel = ViewModelProvider(this, myFactory).get(AddToFavoriteViewModel::class.java)
    }

    private fun extractedDataFromViewModel() {
        lifecycleScope.launch {
            addToFavoriteViewModel.getFilteredMealsByIngredient(filteredMealsByIngredientArgs.ingredientName)
        }
    }

    private fun observeViewModel() {
        addToFavoriteViewModel.filteredMealsList.observe(viewLifecycleOwner) { newList ->
            filteredMealsByIngredientAdapter.filteredMeals = newList.toList()
            filteredMealsByIngredientAdapter.notifyDataSetChanged()
        }


        addToFavoriteViewModel.message.observe(viewLifecycleOwner) {
            Snackbar.make(rvFilteredMealsByIngredient, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onFilteredMealsFavoriteClick(filteredMealsModel: FilteredMealModel?) {
        lifecycleScope.launch {
            addToFavoriteViewModel.saveFilteredMeals(filteredMealsModel)
        }
    }

    override fun onFilteredMealsClick(mealId: String?) {
        val actionFilteredMealsByIngredientFragmentToMealDatailsFragment =
            FilteredMealsByIngredientFragmentDirections.actionFilteredMealsByIngredientFragmentToMealDatailsFragment(mealId)
        findNavController().navigate(actionFilteredMealsByIngredientFragmentToMealDatailsFragment)
    }

}