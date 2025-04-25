package com.example.foodplannerapplication.modules.home.view.fragments
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
import com.example.foodplannerapplication.core.data.models.FilteredMealModel
import com.example.foodplannerapplication.core.data.models.ICommonFilteredMealListener
import com.example.foodplannerapplication.modules.favorite.models.FavoritesDatabase
import com.example.foodplannerapplication.core.data.server.retrofit.RetrofitHelper
import com.example.foodplannerapplication.modules.favorite.viewmodel.AddMealToFavoritesViewModel
import com.example.foodplannerapplication.modules.favorite.viewmodel.AddMealToFavoritesViewModelFactory
import com.example.foodplannerapplication.modules.home.view.adapters.FilteredMealsByAreaAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FilteredMealsByAreaFragment : Fragment(), ICommonFilteredMealListener {
    // arguments
    private val FilteredMealsByAreaFragmentArgs: FilteredMealsByAreaFragmentArgs by navArgs()
    // view model
    private lateinit var addMealToFavoritesViewModel: AddMealToFavoritesViewModel
    // ui components
    private lateinit var filteredMealsByAreaAdapter: FilteredMealsByAreaAdapter
    private lateinit var rvFilteredMealsByArea: RecyclerView
    private lateinit var filteredMeals: List<FilteredMealModel?>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filtered_meals_by_area, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        setUpViewModel()
        extractedDataFromViewModel()
        observeViewModel()
    }

    private fun setupRecyclerView(view: View) {
        rvFilteredMealsByArea = view.findViewById(R.id.rv_filteredMealsByArea)
        filteredMealsByAreaAdapter = FilteredMealsByAreaAdapter(null, requireContext(), false, this)
        rvFilteredMealsByArea.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = filteredMealsByAreaAdapter
        }
    }

    private fun setUpViewModel() {
        var dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        var myFactory = AddMealToFavoritesViewModelFactory(dao, RetrofitHelper)
        addMealToFavoritesViewModel = ViewModelProvider(this, myFactory).get(AddMealToFavoritesViewModel::class.java)
    }

    private fun extractedDataFromViewModel() {
        lifecycleScope.launch {
            addMealToFavoritesViewModel.getFilteredMealsByArea(FilteredMealsByAreaFragmentArgs.areaName)
        }
    }

    private fun observeViewModel() {
        addMealToFavoritesViewModel.filteredMealsList.observe(viewLifecycleOwner) { newList ->
            filteredMealsByAreaAdapter.filteredMeals = newList.toList()
            filteredMealsByAreaAdapter.notifyDataSetChanged()
        }
        addMealToFavoritesViewModel.message.observe(viewLifecycleOwner) {
            Snackbar.make(rvFilteredMealsByArea, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onFilteredMealsFavoriteClick(filteredMealsModel: FilteredMealModel?) {
        lifecycleScope.launch {
            addMealToFavoritesViewModel.saveFilteredMeals(filteredMealsModel)
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