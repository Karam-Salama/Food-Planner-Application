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
import com.example.foodplannerapplication.modules.favorite.viewmodel.AddToFavoriteViewModel
import com.example.foodplannerapplication.modules.favorite.viewmodel.MyFactory
import com.example.foodplannerapplication.core.data.server.retrofit.RetrofitHelper
import com.example.foodplannerapplication.modules.home.view.adapters.FilteredMealsByCategoryAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FilteredMealsByCategoryFragment : Fragment(), ICommonFilteredMealListener {
    // arguments
    private val filteredMealsByCategoryFragmentArgs: FilteredMealsByCategoryFragmentArgs by navArgs()

    // view model
    private lateinit var addToFavoriteViewModel: AddToFavoriteViewModel

    // ui components
    private lateinit var filteredMealsByCategoryAdapter: FilteredMealsByCategoryAdapter
    private lateinit var rvFilteredMealsByCategory: RecyclerView
    private lateinit var filteredMeals: List<FilteredMealModel?>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filtered_meals_by_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView(view)
        setUpViewModel()
        extractedDataFromViewModel()
        observeViewModel()
    }

    private fun setupRecyclerView(view: View) {
        rvFilteredMealsByCategory = view.findViewById(R.id.rv_filteredMealsByCategory)
        filteredMealsByCategoryAdapter = FilteredMealsByCategoryAdapter(null, requireContext(), this)

        rvFilteredMealsByCategory.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = filteredMealsByCategoryAdapter
        }
    }

    private fun setUpViewModel() {
        var dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        var myFactory = MyFactory(dao, RetrofitHelper)
        addToFavoriteViewModel = ViewModelProvider(this, myFactory).get(AddToFavoriteViewModel::class.java)
    }

    private fun extractedDataFromViewModel() {
        lifecycleScope.launch {
            addToFavoriteViewModel.getFilteredMealsByCategory(filteredMealsByCategoryFragmentArgs.categoryName)
        }
    }

    private fun observeViewModel() {
        addToFavoriteViewModel.filteredMealsList.observe(viewLifecycleOwner) { newList ->
            filteredMealsByCategoryAdapter.filteredMeals = newList.toList()
            filteredMealsByCategoryAdapter.notifyDataSetChanged()
        }
        addToFavoriteViewModel.message.observe(viewLifecycleOwner) {
            Snackbar.make(rvFilteredMealsByCategory, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onFilteredMealsFavoriteClick(filteredMealsModel: FilteredMealModel?) {
        lifecycleScope.launch {
            addToFavoriteViewModel.saveFilteredMeals(filteredMealsModel)
        }
    }

    override fun onFilteredMealsClick(mealId: String?) {
        val actionFilteredMealsByCategoryFragmentToMealDatailsFragment =
            FilteredMealsByCategoryFragmentDirections.actionFilteredMealsByCategoryFragmentToMealDatailsFragment(
                mealId
            )
        findNavController().navigate(actionFilteredMealsByCategoryFragmentToMealDatailsFragment)
    }
}