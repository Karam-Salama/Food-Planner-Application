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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.helpers.DialogHelper
import com.example.foodplannerapplication.modules.plans.models.AddMealModel
import com.example.foodplannerapplication.modules.plans.models.AddMealToPlansDatabase
import com.example.foodplannerapplication.modules.plans.viewmodel.AddMealToPlansViewModel
import com.example.foodplannerapplication.modules.plans.viewmodel.AddMealToPlansViewModelFactory

import kotlinx.coroutines.launch

class WeeklyPlansFragment : Fragment(), IWeeklyPlansListener {
    // ui components
    private lateinit var rcWeeklyPlans: RecyclerView
    private lateinit var weeklyPlansAdapter: WeeklyPlansAdapter
    private lateinit var lottieEmptyView: LottieAnimationView
    private lateinit var tvNoPlans: TextView
    // ViewModel
    private lateinit var addMealToPlansViewModel: AddMealToPlansViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
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
        var dao = AddMealToPlansDatabase.getDatabase(requireContext()).getAddMealToPlansDao()
        var myFactory = AddMealToPlansViewModelFactory(dao)
        addMealToPlansViewModel = ViewModelProvider(this, myFactory).get(AddMealToPlansViewModel::class.java)
    }

    private fun extractedDataFromViewModel() {
        lifecycleScope.launch {
            addMealToPlansViewModel.fetchPlans()
        }
    }

    private fun observeViewModel() {
        addMealToPlansViewModel.mealsPlanList.observe(viewLifecycleOwner) { newList ->
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
        DialogHelper.showDeleteConfirmationDialog(
            context = requireContext(),
            message = "Are you sure you want to delete this meal from Plans?",
            onConfirm = {
                lifecycleScope.launch {
                    addMealToPlansViewModel.removePlan(addMealModel)
                }
            },
            onCancel = {}
        )
    }
}

