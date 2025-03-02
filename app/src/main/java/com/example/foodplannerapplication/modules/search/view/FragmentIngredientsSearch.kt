package com.example.foodplannerapplication.modules.search.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.model.cache.room.database.FavoritesDatabase
import com.example.foodplannerapplication.core.viewmodel.AddToFavoriteViewModel
import com.example.foodplannerapplication.core.viewmodel.DashboardViewModel
import com.example.foodplannerapplication.core.viewmodel.MyFactory
import com.example.foodplannerapplication.modules.home.model.server.models.MealModel
import com.example.foodplannerapplication.modules.home.model.server.services.RetrofitHelper
import com.example.foodplannerapplication.modules.home.view.adapters.CategoryAdapter
import com.example.foodplannerapplication.modules.home.view.adapters.IngredientsAdapter


class FragmentIngredientsSearch : Fragment() {
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var ingredientsAdapter: IngredientsAdapter

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ingredients_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewModel()
        setUpRecyclerView(view)
        observeViewModel()
    }

    private fun setUpViewModel(){
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
    }

    private fun setUpRecyclerView(view: View){
        ingredientsRecyclerView = view.findViewById(R.id.rc_ingredients)
        ingredientsAdapter = IngredientsAdapter(listOf(), requireContext())
        ingredientsRecyclerView.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = ingredientsAdapter
        }
    }

    private fun observeViewModel() {
        dashboardViewModel.ingredients.observe(viewLifecycleOwner) { ingredientsList ->
            val ingredientNames = ingredientsList.mapNotNull { it.strIngredient }
            ingredientsAdapter.updateIngredients(ingredientNames)
        }
    }

}