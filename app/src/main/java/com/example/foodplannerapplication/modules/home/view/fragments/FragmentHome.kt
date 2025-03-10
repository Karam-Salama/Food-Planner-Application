package com.example.foodplannerapplication.modules.home.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.model.cache.sharedprefs.CacheHelper
import com.example.foodplannerapplication.core.utils.classes.Constants
import com.example.foodplannerapplication.core.utils.classes.DialogHelper
import com.example.foodplannerapplication.modules.home.model.server.models.MealModel
import com.example.foodplannerapplication.modules.home.model.server.services.RetrofitHelper
import com.example.foodplannerapplication.modules.home.view.adapters.AreaAdapter
import com.example.foodplannerapplication.modules.home.view.adapters.CategoryAdapter
import com.example.foodplannerapplication.core.viewmodel.DashboardViewModel
import com.example.foodplannerapplication.modules.auth.view.LoginActivity
import com.example.foodplannerapplication.modules.home.HomeActivity
import com.example.foodplannerapplication.modules.onboarding.view.OnboardingActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentHome : Fragment() {
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var areaRecyclerView: RecyclerView
    private lateinit var areaAdapter: AreaAdapter

    private lateinit var mealTitle: TextView
    private lateinit var mealImage: ShapeableImageView
    private var mealOfTheDay: MealModel? = null

    private lateinit var randomMealLayout: ConstraintLayout

    private lateinit var addMealFab: FloatingActionButton

    private lateinit var dashboardViewModel: DashboardViewModel


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

        setUpViewModel()
        initViews(view)
        setUpListeners()
        setupActionBar()
        setupNavigationDrawer(view)
        setupFab(view)
        observeViewModel()
        setupRecyclerViews(view)

        fetchRandomMealFromApi()
    }

    // ================================= setUpViewModel ============================================
    private fun setUpViewModel() {
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
    }

    private fun observeViewModel() {
        dashboardViewModel.categories.observe(viewLifecycleOwner) {
            categoryAdapter.updateCategories(it)
        }
        dashboardViewModel.areas.observe(viewLifecycleOwner) {
            areaAdapter.updateAreas(it)
        }
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
            val isAuthSkipClicked = CacheHelper.getBoolean(Constants.OnBording_SKIP_KEY, false)

            if (isAuthSkipClicked) {
                if (Firebase.auth.currentUser == null) {
                    DialogHelper.showLoginRequiredDialog(requireContext())
                } else {
                    findNavController().navigate(R.id.action_fragmentHome_to_addMealFragment)
                }
            } else {
                findNavController().navigate(R.id.action_fragmentHome_to_addMealFragment)
            }
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
            val isAuthSkipClicked = CacheHelper.getBoolean(Constants.OnBording_SKIP_KEY, false)
            if (isAuthSkipClicked) {
                if (Firebase.auth.currentUser == null) {
                    DialogHelper.showLoginRequiredDialog(requireContext())
                } else {
                    val actionFragmentHomeToRandomMealDetailFragment =
                        FragmentHomeDirections.actionFragmentHomeToMealDatailsFragment(mealOfTheDay?.idMeal)
                    findNavController().navigate(actionFragmentHomeToRandomMealDetailFragment)
                }
            } else {
                val actionFragmentHomeToRandomMealDetailFragment =
                    FragmentHomeDirections.actionFragmentHomeToMealDatailsFragment(mealOfTheDay?.idMeal)
                findNavController().navigate(actionFragmentHomeToRandomMealDetailFragment)
            }
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
        val isAuthSkipClicked = CacheHelper.getBoolean(Constants.OnBording_SKIP_KEY, false)

        if (isAuthSkipClicked) {
            if (Firebase.auth.currentUser == null) {
                DialogHelper.showLoginRequiredDialog(requireContext())
            } else {
                val actionFragmentHomeToFilteredMealsByCategoryFragment =
                    FragmentHomeDirections.actionFragmentHomeToFilteredMealsByCategoryFragment(category)
                findNavController().navigate(actionFragmentHomeToFilteredMealsByCategoryFragment)
            }
        } else {
            val actionFragmentHomeToFilteredMealsByCategoryFragment =
                FragmentHomeDirections.actionFragmentHomeToFilteredMealsByCategoryFragment(category)
            findNavController().navigate(actionFragmentHomeToFilteredMealsByCategoryFragment)
        }
    }
    private fun openMealsActivityByArea(area: String?) {

        val isAuthSkipClicked = CacheHelper.getBoolean(Constants.OnBording_SKIP_KEY, false)

        if (isAuthSkipClicked) {
            if (Firebase.auth.currentUser == null) {
                DialogHelper.showLoginRequiredDialog(requireContext())
            } else {
                val actionFragmentHomeToFilteredMealsByAreaFragment =
                    FragmentHomeDirections.actionFragmentHomeToFilteredMealsByAreaFragment(area)
                findNavController().navigate(actionFragmentHomeToFilteredMealsByAreaFragment)
            }
        } else {
            val actionFragmentHomeToFilteredMealsByAreaFragment =
                FragmentHomeDirections.actionFragmentHomeToFilteredMealsByAreaFragment(area)
            findNavController().navigate(actionFragmentHomeToFilteredMealsByAreaFragment)
        }
    }

}
