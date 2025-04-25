package com.example.foodplannerapplication.modules.search.view.fragments
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.favorite.models.FavoritesDatabase
import com.example.foodplannerapplication.core.data.server.retrofit.RetrofitHelper
import com.example.foodplannerapplication.core.helpers.DialogHelper
import com.example.foodplannerapplication.core.helpers.NetworkReceiver
import com.example.foodplannerapplication.modules.favorite.viewmodel.AddMealToFavoritesViewModel
import com.example.foodplannerapplication.modules.home.data.repo.HomeRepository
import com.example.foodplannerapplication.modules.home.viewmodel.FilterType
import com.example.foodplannerapplication.modules.home.viewmodel.HomeAndSearchViewModel
import com.example.foodplannerapplication.modules.search.view.ICommonSearchFilteredListener
import com.example.foodplannerapplication.modules.search.view.adapters.SearchAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText

class FragmentSearch : Fragment(), ICommonSearchFilteredListener {
    // Views
    private lateinit var searchAdapter: SearchAdapter
    // ViewModels
    private lateinit var searchViewModel: HomeAndSearchViewModel
    private lateinit var addMealToFavoritesViewModel: AddMealToFavoritesViewModel
    // For Network Connection
    private lateinit var networkReceiver: NetworkReceiver
    private lateinit var noInternetAnimation: LottieAnimationView
    private lateinit var noInternetText: TextView
    private lateinit var mainContentLayout: View

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setUpRecyclerView(view)
        setupChips(view)
        setupSearch(view)
        setUpFavoriteViewModel()
        setUpViewModel()
        observeViewModel()
    }

    private fun setUpRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = SearchAdapter(requireContext(), this@FragmentSearch)
            searchAdapter = adapter as SearchAdapter
        }
    }

    private fun initViews(view: View) {
        noInternetAnimation = view.findViewById(R.id.noInternetAnimation)
        noInternetText = view.findViewById(R.id.noInternetText)
        mainContentLayout = view.findViewById(R.id.main_content_canstraint_layout)
    }

    override fun onResume() {
        super.onResume()
        networkReceiver = NetworkReceiver { isConnected ->
            handleInternetChange(isConnected)
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(networkReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(networkReceiver)
    }

    private fun handleInternetChange(isConnected: Boolean) {
        if (isConnected) {
            noInternetAnimation.visibility = View.GONE
            noInternetText.visibility = View.GONE
            mainContentLayout.visibility = View.VISIBLE
        } else {
            noInternetAnimation.visibility = View.VISIBLE
            noInternetText.visibility = View.VISIBLE
            mainContentLayout.visibility = View.GONE
        }
    }

    private fun setupChips(view: View) {
        view.findViewById<Chip>(R.id.chipCategories).setOnClickListener {
            searchViewModel.setFilterType(FilterType.CATEGORIES)
        }
        view.findViewById<Chip>(R.id.chipCountries).setOnClickListener {
            searchViewModel.setFilterType(FilterType.COUNTRIES)
        }
        view.findViewById<Chip>(R.id.chipIngredients).setOnClickListener {
            searchViewModel.setFilterType(FilterType.INGREDIENTS)
        }
    }

    private fun setUpViewModel() {
        searchViewModel = HomeAndSearchViewModel(HomeRepository())
    }

    private fun setupSearch(view: View) {
        view.findViewById<TextInputEditText>(R.id.et_meals_search).addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {searchViewModel.setSearchQuery(s.toString())}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
        )
    }

    private fun observeViewModel() {
        searchViewModel.filteredData.observe(viewLifecycleOwner) {
            searchAdapter.updateList(it)
        }
    }

    private fun setUpFavoriteViewModel() {
        val dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        val retrofitHelper = RetrofitHelper
        addMealToFavoritesViewModel = AddMealToFavoritesViewModel(dao,retrofitHelper)
    }

    private fun shouldShowLoginDialog(): Boolean {
        val isGuestUser = searchViewModel.isGuestUser()
        val isUnAuthenticatedUser = !searchViewModel.isUserLoggedIn()
        return isGuestUser && isUnAuthenticatedUser
    }

    override fun openMealsActivityByCategory(category: String?) {
        if (shouldShowLoginDialog()) {
            DialogHelper.showLoginRequiredDialog(requireContext())
        } else {
            findNavController().navigate(
                FragmentSearchDirections.actionFragmentSearchToFilteredMealsByCategoryFragment(category)
            )
        }
    }

    override fun openMealsActivityByArea(area: String?) {
        if (shouldShowLoginDialog()) {
            DialogHelper.showLoginRequiredDialog(requireContext())
        } else {
            findNavController().navigate(
                FragmentSearchDirections.actionFragmentSearchToFilteredMealsByAreaFragment(area)
            )
        }
    }

    override fun openMealsActivityByIngredient(ingredient: String?) {
        if (shouldShowLoginDialog()) {
            DialogHelper.showLoginRequiredDialog(requireContext())
        } else {
            findNavController().navigate(
                FragmentSearchDirections.actionFragmentSearchToFilteredMealsByIngredientFragment(ingredient)
            )
        }
    }

    override fun onFilteredMealsClick(mealId: String?) {
        // Handle meal click if needed
    }
}