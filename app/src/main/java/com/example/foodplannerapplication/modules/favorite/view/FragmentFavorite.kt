package com.example.foodplannerapplication.modules.favorite.view
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.data.models.FilteredMealModel
import com.example.foodplannerapplication.core.data.models.ICommonFilteredMealListener
import com.example.foodplannerapplication.core.helpers.DialogHelper
import com.example.foodplannerapplication.core.data.server.retrofit.RetrofitHelper
import com.example.foodplannerapplication.modules.favorite.models.FavoritesDatabase
import com.example.foodplannerapplication.modules.favorite.viewmodel.AddMealToFavoritesViewModel
import com.example.foodplannerapplication.modules.favorite.viewmodel.AddMealToFavoritesViewModelFactory
import com.example.foodplannerapplication.modules.home.view.adapters.FilteredMealsByAreaAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FragmentFavorite : Fragment(), ICommonFilteredMealListener {
    private lateinit var rvFavorite: RecyclerView
    private lateinit var favoriteAdapter: FilteredMealsByAreaAdapter
    private lateinit var lottieEmptyFavorites: LottieAnimationView
    private lateinit var tvNoFavorites: TextView
    private lateinit var viewModel: AddMealToFavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupViewModel()
        setupAdapter()
        setupObservers()
        loadFavorites()
    }

    private fun initViews(view: View) {
        rvFavorite = view.findViewById(R.id.rc_favorite_meals)
        lottieEmptyFavorites = view.findViewById(R.id.lottie_empty_favorites)
        tvNoFavorites = view.findViewById(R.id.tv_no_favorites)
    }

    private fun setupViewModel() {
        val dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        val factory = AddMealToFavoritesViewModelFactory(dao, RetrofitHelper)
        viewModel = ViewModelProvider(this, factory).get(AddMealToFavoritesViewModel::class.java)
    }

    private fun setupAdapter() {
        favoriteAdapter = FilteredMealsByAreaAdapter(
            context = requireContext(),
            isFavoriteScreen = true,
            listener = this
        )

        rvFavorite.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = favoriteAdapter
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
    }

    private fun setupObservers() {
        viewModel.filteredMealsList.observe(viewLifecycleOwner) { favorites ->
            updateUI(favorites)
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message?.let { showSnackbar(it) }
        }
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            viewModel.fetchFavorites()
            viewModel.listenForFirebaseFavorites() // بدء الاستماع لتحديثات Firebase
        }
    }

    private fun updateUI(favorites: List<FilteredMealModel?>) {
        if (favorites.isNullOrEmpty()) {
            showEmptyState()
        } else {
            showFavoritesList(favorites.filterNotNull())
        }
    }

    private fun showEmptyState() {
        rvFavorite.visibility = View.GONE
        lottieEmptyFavorites.visibility = View.VISIBLE
        tvNoFavorites.visibility = View.VISIBLE
    }

    private fun showFavoritesList(favorites: List<FilteredMealModel>) {
        rvFavorite.visibility = View.VISIBLE
        lottieEmptyFavorites.visibility = View.GONE
        tvNoFavorites.visibility = View.GONE

        favoriteAdapter.updateList(favorites.map { it.copy(isFavorite = true) })
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(rvFavorite, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onFilteredMealsFavoriteClick(meal: FilteredMealModel?) {
        meal?.let { mealItem ->
            DialogHelper.showDeleteConfirmationDialog(
                context = requireContext(),
                message = "Are you sure you want to delete this meal from favorites?",
                onConfirm = {
                    lifecycleScope.launch {
                        viewModel.removeFilteredMeals(mealItem)
                    }
                },
                onCancel = {
                }
            )
        }
    }

    override fun onFilteredMealsClick(mealId: String?) {
        mealId?.let {
            val action = FragmentFavoriteDirections
                .actionFragmentFavoriteToMealDatailsFragment(mealId)
            findNavController().navigate(action)
        }
    }
}