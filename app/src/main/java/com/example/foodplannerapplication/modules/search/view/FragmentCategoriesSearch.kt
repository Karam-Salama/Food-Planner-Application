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
import com.example.foodplannerapplication.core.viewmodel.DashboardViewModel
import com.example.foodplannerapplication.modules.home.view.adapters.AreaAdapter
import com.example.foodplannerapplication.modules.home.view.adapters.CategoryAdapter


class FragmentCategoriesSearch : Fragment() {
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerViews(view)
        setUpViewModel()
        observeViewModel()
    }

    private fun setUpRecyclerViews(view: View) {
        categoryRecyclerView = view.findViewById(R.id.rc_categories)
        categoryAdapter = CategoryAdapter(null, requireContext(), { })
        categoryRecyclerView.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(),3)
            adapter = categoryAdapter
        }
    }
    private fun setUpViewModel() {
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
    }

    private fun observeViewModel() {
        dashboardViewModel.categories.observe(viewLifecycleOwner) {
            categoryAdapter.updateCategories(it)
        }
    }
}