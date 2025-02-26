package com.example.foodplannerapplication.modules.favorite.view
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlinx.coroutines.launch

class FragmentFavorite : Fragment() , ICommonFilteredMealListener {

    private lateinit var rvFavorite: RecyclerView
    private lateinit var favoriteAdapter: FilteredMealsByAreaAdapter
    private lateinit var addToFavoriteViewModel: AddToFavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setUpViewModel()
        extractedDataFromViewModel()
        observeViewModel()
    }

    private fun initViews(view: View) {
        rvFavorite = view.findViewById(R.id.rc_favorite_meals)
        favoriteAdapter = FilteredMealsByAreaAdapter(null, requireContext(), this)

        rvFavorite.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = favoriteAdapter
        }
    }

    private fun setUpViewModel() {
        var dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        var myFactory = MyFactory(dao, RetrofitHelper)
        addToFavoriteViewModel = ViewModelProvider(this, myFactory).get(AddToFavoriteViewModel::class.java)
    }

    private fun extractedDataFromViewModel() {
        lifecycleScope.launch {
            addToFavoriteViewModel.fetchFavorites()
        }
    }

    private fun observeViewModel() {
        addToFavoriteViewModel.filteredMealsList.observe(viewLifecycleOwner) { newList ->
            favoriteAdapter.filteredMeals = newList.toList()
            favoriteAdapter.notifyDataSetChanged()
        }


        addToFavoriteViewModel.message.observe(viewLifecycleOwner) {
            Snackbar.make(rvFavorite, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onFilteredMealsFavoriteClick(filteredMealsModel: FilteredMealModel?) {
        lifecycleScope.launch {
            addToFavoriteViewModel.removeFilteredMeals(filteredMealsModel)
        }
    }

    override fun onFilteredMealsClick(mealId: String?) {
        val actionFragmentFavoriteToMealDatailsFragment =
            FragmentFavoriteDirections.actionFragmentFavoriteToMealDatailsFragment(mealId)
        findNavController().navigate(actionFragmentFavoriteToMealDatailsFragment)
    }
}