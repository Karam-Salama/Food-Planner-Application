package com.example.foodplannerapplication.modules.home.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.home.data.models.CategoryModel
import com.example.foodplannerapplication.modules.home.data.services.RetrofitHelper
import com.example.foodplannerapplication.modules.home.ui.FilteredMealsActivity
import com.example.foodplannerapplication.modules.home.ui.adapters.CategoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesFragment : Fragment() {
    // ================================= Global Variables ==========================================
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private var categories: List<CategoryModel?>? = null

    // ================================= onCreateView()  ===========================================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    // ================================= onViewCreated() ===========================================
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        fetchDataFromApi()
    }

    // ================================= setupRecyclerView =========================================
    private fun setupRecyclerView(view: View) {
        categoryRecyclerView = view.findViewById(R.id.rv_categories)
        categoryAdapter = CategoryAdapter(null, requireContext()) { category ->
            openMealsActivity(category)
        }

        categoryRecyclerView.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = categoryAdapter
        }
    }

    private fun openMealsActivity(category: String?) {
        val intent = Intent(requireContext(), FilteredMealsActivity::class.java)
        intent.putExtra("CATEGORY_NAME", category)
        startActivity(intent)
    }

    // ================================= fetchDataFromApi ==========================================
    private fun fetchDataFromApi() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getCategories()
                val categories = response.categories.orEmpty()

                withContext(Dispatchers.Main) {
                    categoryAdapter.updateCategories(categories)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("===>", "Error Fetching Categories", e)
                }
            }
        }
    }
}
