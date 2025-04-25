package com.example.foodplannerapplication.modules.home.view.fragments
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.helpers.DialogHelper
import com.example.foodplannerapplication.core.helpers.NetworkReceiver
import com.example.foodplannerapplication.modules.home.data.repo.HomeRepository
import com.example.foodplannerapplication.modules.home.view.adapters.AreaAdapter
import com.example.foodplannerapplication.modules.home.view.adapters.CategoryAdapter
import com.example.foodplannerapplication.modules.home.viewmodel.HomeAndSearchViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.imageview.ShapeableImageView

class FragmentHome : Fragment() {
    private lateinit var randomMealLayout: ConstraintLayout
    private lateinit var mealTitle: TextView
    private lateinit var mealImage: ShapeableImageView
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var areaRecyclerView: RecyclerView
    private lateinit var areaAdapter: AreaAdapter
    private lateinit var addMealFab: FloatingActionButton
    private lateinit var networkReceiver: NetworkReceiver
    private lateinit var noInternetAnimation: LottieAnimationView
    private lateinit var noInternetText: TextView
    private lateinit var mainContentLayout: View
    private lateinit var viewModel: HomeAndSearchViewModel

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setUpViewModel()
        setupObservers()
        setupListeners()
        setupActionBar()
        setupNavigationDrawer(view)
        setupFab(view)
        setupRecyclerViews(view)
    }

    override fun onResume() {
        super.onResume()
        setupNetworkReceiver()
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(networkReceiver)
    }

    private fun setupNetworkReceiver() {
        networkReceiver = NetworkReceiver { isConnected ->
            handleInternetChange(isConnected)
        }
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        requireContext().registerReceiver(networkReceiver, intentFilter)
    }

    private fun handleInternetChange(isConnected: Boolean) {
        if (isConnected) {
            noInternetAnimation.visibility = View.GONE
            noInternetText.visibility = View.GONE
            mainContentLayout.visibility = View.VISIBLE
            addMealFab.visibility = View.VISIBLE
        } else {
            noInternetAnimation.visibility = View.VISIBLE
            noInternetText.visibility = View.VISIBLE
            mainContentLayout.visibility = View.GONE
            addMealFab.visibility = View.GONE
        }
    }

    private fun initViews(view: View) {
        mealTitle = view.findViewById(R.id.mealTitle)
        mealImage = view.findViewById(R.id.mealImage)
        randomMealLayout = view.findViewById(R.id.mealContainer)
        noInternetAnimation = view.findViewById(R.id.noInternetAnimation)
        noInternetText = view.findViewById(R.id.noInternetText)
        mainContentLayout = view.findViewById(R.id.main_content_canstraint_layout)
    }

    private fun setUpViewModel() {
        viewModel = HomeAndSearchViewModel(HomeRepository())
    }

    private fun setupObservers() {
        viewModel.randomMeal.observe(viewLifecycleOwner) { meal ->
            meal?.let {
                mealTitle.text = it.strMeal
                loadMealImage(it.strMealThumb ?: "")
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.updateCategories(categories)
        }

        viewModel.areas.observe(viewLifecycleOwner) { areas ->
            areaAdapter.updateAreas(areas)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Handle loading state if needed
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                // Show error message if needed
            }
        }
    }

    private fun setupListeners() {
        randomMealLayout.setOnClickListener {
            if (shouldShowLoginDialog()) {
                DialogHelper.showLoginRequiredDialog(requireContext())
            } else {
                viewModel.randomMeal.value?.idMeal?.let { mealId ->
                    navigateToMealDetails(mealId)
                }
            }
        }
    }

    private fun shouldShowLoginDialog(): Boolean =
        viewModel.isGuestUser() && !viewModel.isUserLoggedIn()

    private fun setupActionBar() {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupNavigationDrawer(view: View) {
        val drawerLayout = (activity as? AppCompatActivity)?.findViewById<DrawerLayout>(R.id.main)
        view.findViewById<Toolbar>(R.id.tool_bar)?.setNavigationOnClickListener {
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    private fun setupFab(view: View) {
        addMealFab = view.findViewById(R.id.add_meal_fab)
        addMealFab.setOnClickListener {
            if (shouldShowLoginDialog()) {
                DialogHelper.showLoginRequiredDialog(requireContext())
            } else {
                navigateToAddMeal()
            }
        }
    }

    private fun loadMealImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_ic)
            .into(mealImage)
    }

    private fun setupRecyclerViews(view: View) {
        setupCategoryRecyclerView(view)
        setupAreaRecyclerView(view)
    }

    private fun setupCategoryRecyclerView(view: View) {
        categoryRecyclerView = view.findViewById(R.id.rv_categories)
        categoryAdapter = CategoryAdapter(emptyList(), requireContext()) { category ->
            if (shouldShowLoginDialog()) {
                DialogHelper.showLoginRequiredDialog(requireContext())
            } else {
                navigateToFilteredMealsByCategory(category)
            }
        }

        categoryRecyclerView.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
    }

    private fun setupAreaRecyclerView(view: View) {
        areaRecyclerView = view.findViewById(R.id.rv_areas)
        areaAdapter = AreaAdapter(emptyList(), requireContext()) { area ->
            if (shouldShowLoginDialog()) {
                DialogHelper.showLoginRequiredDialog(requireContext())
            } else {
                navigateToFilteredMealsByArea(area)
            }
        }

        areaRecyclerView.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = areaAdapter
        }
    }

    private fun navigateToMealDetails(mealId: String) {
        findNavController().navigate(
            FragmentHomeDirections.actionFragmentHomeToMealDatailsFragment(mealId)
        )
    }

    private fun navigateToAddMeal() {
        findNavController().navigate(R.id.action_fragmentHome_to_addMealFragment)
    }

    private fun navigateToFilteredMealsByCategory(category: String?) {
        findNavController().navigate(
            FragmentHomeDirections.actionFragmentHomeToFilteredMealsByCategoryFragment(category)
        )
    }

    private fun navigateToFilteredMealsByArea(area: String?) {
        findNavController().navigate(
            FragmentHomeDirections.actionFragmentHomeToFilteredMealsByAreaFragment(area)
        )
    }
}