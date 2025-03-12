package com.example.foodplannerapplication.modules.plans.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.model.FilteredMealModel
import com.example.foodplannerapplication.core.model.ICommonFilteredMealListener
import com.example.foodplannerapplication.core.model.cache.room.database.FavoritesDatabase
import com.example.foodplannerapplication.core.utils.classes.DialogHelper
import com.example.foodplannerapplication.core.viewmodel.AddToFavoriteViewModel
import com.example.foodplannerapplication.core.viewmodel.MyFactory
import com.example.foodplannerapplication.modules.home.model.cache.database.AddMealDatabase
import com.example.foodplannerapplication.modules.home.model.cache.entity.AddMealModel
import com.example.foodplannerapplication.modules.home.model.server.services.RetrofitHelper
import com.example.foodplannerapplication.modules.home.view.adapters.FilteredMealsByAreaAdapter
import com.example.foodplannerapplication.modules.home.viewmodel.AddMealViewModel
import kotlinx.coroutines.launch


class WeeklyPlansFragment : Fragment(), IWeeklyPlansListener {
    private lateinit var rcWeeklyPlans: RecyclerView
    private lateinit var weeklyPlansAdapter: WeeklyPlansAdapter
    private lateinit var weeklyPlansViewModel: AddMealViewModel

    private lateinit var lottieEmptyView: LottieAnimationView
    private lateinit var tvNoPlans: TextView



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly_plans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupRecyclerView(view)
        setUpViewModel()
        extractedDataFromViewModel()
        observeViewModel()
    }

    private fun initViews(view: View) {
        lottieEmptyView = view.findViewById(R.id.lottie_empty_view)
        tvNoPlans = view.findViewById(R.id.tv_no_plans)
    }

    private fun setupRecyclerView(view: View) {
        rcWeeklyPlans = view.findViewById(R.id.rc_weekly_plans)
        weeklyPlansAdapter = WeeklyPlansAdapter(listOf(),requireContext(),this)
        rcWeeklyPlans.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = weeklyPlansAdapter
        }
    }

    private fun setUpViewModel() {
        var dao = AddMealDatabase.getDatabase(requireContext()).getAddMealDao()
        var myFactory = com.example.foodplannerapplication.modules.home.viewmodel.MyFactory(dao)
        weeklyPlansViewModel = ViewModelProvider(this, myFactory).get(AddMealViewModel::class.java)
    }

    private fun extractedDataFromViewModel() {
        lifecycleScope.launch {
            weeklyPlansViewModel.fetchPlans()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        weeklyPlansViewModel.mealsPlanList.observe(viewLifecycleOwner) { newList ->
            if (newList.isNullOrEmpty()) {
                rcWeeklyPlans.visibility = View.GONE
                lottieEmptyView.visibility = View.VISIBLE
                tvNoPlans.visibility = View.VISIBLE
            } else {
                rcWeeklyPlans.visibility = View.VISIBLE
                lottieEmptyView.visibility = View.GONE
                tvNoPlans.visibility = View.GONE
                weeklyPlansAdapter.weeklyMeals = newList.toList()
                weeklyPlansAdapter.notifyDataSetChanged()
            }
        }
    }


    override fun onDeleteWeeklyPlansClick(addMealModel: AddMealModel) {
        DialogHelper.showDeleteConfirmationDialog(requireContext()) {
            lifecycleScope.launch {
                weeklyPlansViewModel.removePlan(addMealModel)
            }
        }
    }
}

