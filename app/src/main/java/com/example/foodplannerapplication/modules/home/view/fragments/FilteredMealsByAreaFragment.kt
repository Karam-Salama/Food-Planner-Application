package com.example.foodplannerapplication.modules.home.view.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
    private val args: FilteredMealsByAreaFragmentArgs by navArgs()
    private lateinit var addMealToFavoritesViewModel: AddMealToFavoritesViewModel
    private lateinit var rvFilteredMealsByArea: RecyclerView
    private lateinit var adapter: FilteredMealsByAreaAdapter

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filtered_meals_by_area, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        setUpViewModel()
        fetchData()
        observeViewModel()
    }

    private fun setupRecyclerView(view: View) {
        rvFilteredMealsByArea = view.findViewById(R.id.rv_filteredMealsByArea)
        adapter = FilteredMealsByAreaAdapter(
            context = requireContext(),
            isFavoriteScreen = false,
            listener = this
        )
        rvFilteredMealsByArea.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@FilteredMealsByAreaFragment.adapter
        }
    }

    private fun setUpViewModel() {
        val dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        val factory = AddMealToFavoritesViewModelFactory(dao, RetrofitHelper)
        addMealToFavoritesViewModel = ViewModelProvider(this, factory).get(AddMealToFavoritesViewModel::class.java)
    }

    private fun fetchData() {
        lifecycleScope.launch {
            addMealToFavoritesViewModel.getFilteredMealsByArea(args.areaName)
        }
    }

    private fun observeViewModel() {
        addMealToFavoritesViewModel.filteredMealsList.observe(viewLifecycleOwner) { newList ->
            adapter.updateList(newList.filterNotNull())
        }

        addMealToFavoritesViewModel.message.observe(viewLifecycleOwner) { message ->
            message?.let {
                Snackbar.make(rvFilteredMealsByArea, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onFilteredMealsFavoriteClick(filteredMealsModel: FilteredMealModel?) {
        lifecycleScope.launch {
                addMealToFavoritesViewModel.saveFilteredMeals(filteredMealsModel)
            }

    }

    override fun onFilteredMealsClick(mealId: String?) {
        mealId?.let {
            val action = FilteredMealsByAreaFragmentDirections
                .actionFilteredMealsByAreaFragmentToMealDatailsFragment(mealId)
            findNavController().navigate(action)
        }
    }
}