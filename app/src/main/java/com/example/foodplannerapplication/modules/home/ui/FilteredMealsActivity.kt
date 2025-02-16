package com.example.foodplannerapplication.modules.home.ui

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.home.data.services.RetrofitHelper
import com.example.foodplannerapplication.modules.home.ui.adapters.CategoryAdapter
import com.example.foodplannerapplication.modules.home.ui.adapters.FilteredMealsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FilteredMealsActivity : AppCompatActivity() {
    private lateinit var categoryName: String
    private lateinit var tvCategoryName: TextView
    private lateinit var filteredMealsRecyclerView: RecyclerView
    private lateinit var filteredMealsAdapter: FilteredMealsAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_filtered_meals)

        initViewsAndSetupCategoryName()
        setupRecyclerView()
        fetchDataFromApi()
    }
    // ================================= initViews =================================================
    private fun initViewsAndSetupCategoryName() {
        tvCategoryName = findViewById(R.id.categoryName)
        categoryName = intent.getStringExtra("CATEGORY_NAME") ?: ""
        tvCategoryName.text = categoryName
    }
    // ================================= setupRecyclerView =========================================
    class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // رقم العنصر داخل القائمة
            if (position == RecyclerView.NO_POSITION) return

            val column = position % spanCount // تحديد العمود الذي يوجد به العنصر

            with(outRect) {
                left = spacing - column * spacing / spanCount
                right = (column + 1) * spacing / spanCount

                if (position < spanCount) {
                    top = spacing // تباعد علوي للعناصر الأولى
                }
                bottom = spacing // تباعد سفلي لكل العناصر
            }
        }
    }


    private fun setupRecyclerView() {
        filteredMealsRecyclerView = findViewById(R.id.rv_filteredMealsList)
        filteredMealsAdapter = FilteredMealsAdapter(null, this)

        filteredMealsRecyclerView.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(this@FilteredMealsActivity, 2)
            adapter = filteredMealsAdapter
        }
    }


    // ================================= fetchDataFromApi ==========================================
    private fun fetchDataFromApi() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getMealsByCategory(categoryName)
                val filteredMeals = response.meals.orEmpty()

                withContext(Dispatchers.Main) {
                    filteredMealsAdapter.updateFilteredMeals(filteredMeals)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("===>", "Error Fetching Filtered Meals", e)
                }
            }
        }
    }
}