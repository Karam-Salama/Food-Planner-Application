package com.example.foodplannerapplication.modules.search.view
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.model.FilteredMealModel
import com.example.foodplannerapplication.core.model.ICommonFilteredMealListener
import com.example.foodplannerapplication.core.model.cache.room.database.FavoritesDatabase
import com.example.foodplannerapplication.core.viewmodel.AddToFavoriteViewModel
import com.example.foodplannerapplication.core.viewmodel.DashboardViewModel
import com.example.foodplannerapplication.core.viewmodel.FilterType
import com.example.foodplannerapplication.core.viewmodel.MyFactory
import com.example.foodplannerapplication.modules.home.model.server.services.RetrofitHelper
import com.example.foodplannerapplication.modules.search.view.adapters.SearchAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class FragmentSearch : Fragment(), ICommonFilteredMealListener {
    private lateinit var viewModel: DashboardViewModel
    private lateinit var addToFavoriteViewModel: AddToFavoriteViewModel
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = SearchAdapter(requireContext(), this@FragmentSearch).also { searchAdapter = it }
        }

        setupChips(view)
        setupSearch(view)
        setUpFavoriteViewModel()
        observeViewModel()
    }

    private fun setupChips(view: View) {
        view.findViewById<Chip>(R.id.chipCategories).setOnClickListener { viewModel.setFilterType(FilterType.CATEGORIES) }
        view.findViewById<Chip>(R.id.chipCountries).setOnClickListener { viewModel.setFilterType(FilterType.COUNTRIES) }
        view.findViewById<Chip>(R.id.chipIngredients).setOnClickListener { viewModel.setFilterType(FilterType.INGREDIENTS) }
    }

    private fun setupSearch(view: View) {
        view.findViewById<TextInputEditText>(R.id.et_meals_search).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { viewModel.setSearchQuery(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun observeViewModel() {
        viewModel.filteredData.observe(viewLifecycleOwner) { searchAdapter.updateList(it) }
    }

    private fun setUpFavoriteViewModel() {
        var dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        var myFactory = MyFactory(dao, RetrofitHelper)
        addToFavoriteViewModel = ViewModelProvider(this, myFactory).get(AddToFavoriteViewModel::class.java)
    }

    override fun onFilteredMealsFavoriteClick(filteredMealsModel: FilteredMealModel?) {
        lifecycleScope.launch {
            addToFavoriteViewModel.saveFilteredMeals(filteredMealsModel)
        }
    }
    override fun onFilteredMealsClick(mealId: String?) {

    }
}
