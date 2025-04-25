package com.example.foodplannerapplication.modules.favorite.view
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.data.models.FilteredMealModel
import com.example.foodplannerapplication.core.data.models.ICommonFilteredMealListener
import com.example.foodplannerapplication.modules.favorite.models.FavoritesDatabase
import com.example.foodplannerapplication.core.helpers.DialogHelper
import com.example.foodplannerapplication.core.data.server.retrofit.RetrofitHelper
import com.example.foodplannerapplication.modules.favorite.viewmodel.AddMealToFavoritesViewModel
import com.example.foodplannerapplication.modules.favorite.viewmodel.AddMealToFavoritesViewModelFactory
import com.example.foodplannerapplication.modules.home.view.adapters.FilteredMealsByAreaAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FragmentFavorite : Fragment() , ICommonFilteredMealListener {
    // Ui Components
    private lateinit var rvFavorite: RecyclerView
    private lateinit var favoriteAdapter: FilteredMealsByAreaAdapter
    private lateinit var lottieEmptyFavorites: LottieAnimationView
    private lateinit var tvNoFavorites: TextView
    // viewModel
    private lateinit var addMealToFavoritesViewModel: AddMealToFavoritesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
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
        lottieEmptyFavorites = view.findViewById(R.id.lottie_empty_favorites)
        tvNoFavorites = view.findViewById(R.id.tv_no_favorites)
    }

    private fun setUpViewModel() {
        var dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        var myFactory = AddMealToFavoritesViewModelFactory(dao, RetrofitHelper)
        addMealToFavoritesViewModel = ViewModelProvider(this, myFactory).get(AddMealToFavoritesViewModel::class.java)
    }

    private fun extractedDataFromViewModel() {
        lifecycleScope.launch {
            addMealToFavoritesViewModel.fetchFavorites()
        }
    }

    private fun observeViewModel() {
        addMealToFavoritesViewModel.filteredMealsList.observe(viewLifecycleOwner) { newList ->
            if (newList.isNullOrEmpty()) {
                rvFavorite.visibility = View.GONE
                lottieEmptyFavorites.visibility = View.VISIBLE
                tvNoFavorites.visibility = View.VISIBLE
            } else {
                rvFavorite.visibility = View.VISIBLE
                lottieEmptyFavorites.visibility = View.GONE
                tvNoFavorites.visibility = View.GONE
                favoriteAdapter.filteredMeals = newList.toList()
                favoriteAdapter.notifyDataSetChanged()
            }
        }

        addMealToFavoritesViewModel.message.observe(viewLifecycleOwner) {
            Snackbar.make(rvFavorite, it, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onFilteredMealsFavoriteClick(filteredMealsModel: FilteredMealModel?) {
        DialogHelper.showDeleteConfirmationDialog(requireContext()) {
            lifecycleScope.launch {
                addMealToFavoritesViewModel.removeFilteredMeals(filteredMealsModel)
            }
        }
    }

    override fun onFilteredMealsClick(mealId: String?) {
        val actionFragmentFavoriteToMealDatailsFragment =
            FragmentFavoriteDirections.actionFragmentFavoriteToMealDatailsFragment(mealId)
        findNavController().navigate(actionFragmentFavoriteToMealDatailsFragment)
    }
}