package com.example.foodplannerapplication.modules.home.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.home.data.server.models.AreaModel
import com.example.foodplannerapplication.modules.home.data.server.models.CategoryModel
import com.example.foodplannerapplication.modules.home.data.server.models.MealModel
import com.example.foodplannerapplication.modules.home.data.server.services.RetrofitHelper
import com.example.foodplannerapplication.modules.home.ui.adapters.AreaAdapter
import com.example.foodplannerapplication.modules.home.ui.adapters.CategoryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentHome : Fragment() {
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private var categories: List<CategoryModel?>? = null

    private lateinit var areaRecyclerView: RecyclerView
    private lateinit var areaAdapter: AreaAdapter
    private var areas: List<AreaModel?>? = null

    private lateinit var mealTitle: TextView
    private lateinit var mealImage: ShapeableImageView
    private var mealOfTheDay: MealModel? = null

    private lateinit var randomMealLayout: ConstraintLayout

    private lateinit var addMealFab: FloatingActionButton

    // ================================ onCreateView ===============================================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    // ================================= onViewCreated =============================================
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setUpListeners()
        setupActionBar()
        setupNavigationDrawer(view)
        setupFab(view)
        fetchRandomMealFromApi()
        setupRecyclerViews(view)
        fetchCategoriesFromApi()
        fetchAreasFromApi()
    }

    // ================================= Setup ActionBar ===========================================
    private fun setupActionBar() {
        val activity = activity as? AppCompatActivity
        activity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // ================================ Setup Navigation Drawer ====================================
    private fun setupNavigationDrawer(view: View) {
        val activity = activity as? AppCompatActivity
        val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.main)
        view.findViewById<Toolbar>(R.id.tool_bar)?.setNavigationOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    // ================================ Setup Floating Action Button  ==============================
    private fun setupFab(view: View) {
        addMealFab = view.findViewById(R.id.add_meal_fab)
        addMealFab.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHome_to_addMealFragment)
        }
    }

    // ================================= initViews For Random Meal =================================
    private fun initViews(view: View) {
        mealTitle = view.findViewById(R.id.mealTitle)
        mealImage = view.findViewById(R.id.mealImage)
        randomMealLayout = view.findViewById(R.id.mealContainer)
    }

    private fun setUpListeners() {
        randomMealLayout.setOnClickListener {
            val actionFragmentHomeToRandomMealDetailFragment =
                FragmentHomeDirections.actionFragmentHomeToRandomMealDetailFragment(mealOfTheDay?.idMeal)
            findNavController().navigate(actionFragmentHomeToRandomMealDetailFragment)
        }
    }

    // =============== fetchRandomMealFromApi & loadMealImage Using Glide ==========================
    private fun fetchRandomMealFromApi() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getMealOfTheDay()
                mealOfTheDay = response.meals?.firstOrNull()
                withContext(Dispatchers.Main) {
                    mealTitle.text = mealOfTheDay?.strMeal
                    mealOfTheDay?.strMealThumb?.let { loadMealImage(it) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("===>", "Error Fetching Meal of the Day", e)
                }
            }
        }
    }

    private fun loadMealImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_ic)
            .into(mealImage)
    }

    // =============== setupRecyclerViews & fetchCategoriesFromApi &  fetchAreasFromApi ============
    private fun setupRecyclerViews(view: View) {
        // ========================== Category RecyclerView ========================================
        categoryRecyclerView = view.findViewById(R.id.rv_categories)
        categoryAdapter = CategoryAdapter(null, requireContext()) { category -> openMealsActivityByCategory(category) }

        categoryRecyclerView.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }

        // ========================== Area RecyclerView ============================================
        areaRecyclerView = view.findViewById(R.id.rv_areas)
        areaAdapter = AreaAdapter(null, requireContext(), { area -> openMealsActivityByArea(area) })
        areaRecyclerView.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(),3)
            adapter = areaAdapter
        }
    }


    private fun openMealsActivityByCategory(category: String?) {
        val actionFragmentHomeToFilteredMealsByCategoryFragment =
            FragmentHomeDirections.actionFragmentHomeToFilteredMealsByCategoryFragment(category)
        findNavController().navigate(actionFragmentHomeToFilteredMealsByCategoryFragment)
    }
    private fun openMealsActivityByArea(area: String?) {
        val actionFragmentHomeToFilteredMealsByAreaFragment =
            FragmentHomeDirections.actionFragmentHomeToFilteredMealsByAreaFragment(area)
        findNavController().navigate(actionFragmentHomeToFilteredMealsByAreaFragment)
    }

    private fun fetchCategoriesFromApi() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getCategories()
                val categories = response.categories.orEmpty()
                Log.d("===>", "=================== Categories: ${categories.size} =================")
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

    private fun fetchAreasFromApi(){
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getAreas()
                val areas = response.meals.orEmpty()
                Log.d("===>", "=================== Areas: ${areas.size} =================")
                withContext(Dispatchers.Main) {
                    areaAdapter.updateAreas(areas)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("===>", "Error Fetching Areas", e)
                }
            }
        }
    }
}
