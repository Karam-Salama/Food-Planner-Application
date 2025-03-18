package com.example.foodplannerapplication.modules.home
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.foodplannerapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        initViews()
        setSupportActionBar(toolbar)

        initNav()
        toggle = setupActionBarDrawerToggle()
        handleLeadingToolbarIcon()
        handleLeadingToolbarTitle()

        NavigationUI.setupWithNavController(navView, navController)
        bottomNavigationView.setupWithNavController(navController)
    }

    // =============================== init Views & Nav Controller =================================
    private fun initViews() {
        navView = findViewById(R.id.nav_view)
        drawerLayout = findViewById(R.id.main)
        bottomNavigationView = findViewById(R.id.bottom_nav_view)
        toolbar = findViewById(R.id.tool_bar)
    }

    private fun initNav() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigationView.visibility = when (destination.id) {
                R.id.addMealFragment ->View.GONE
                R.id.filteredMealsByCategoryFragment ->View.GONE
                R.id.filteredMealsByAreaFragment ->View.GONE
                R.id.mealDatailsFragment ->View.GONE
                R.id.personalInformationFragment ->View.GONE
                R.id.aboutUsFragment ->View.GONE
                else -> View.VISIBLE
            }
            invalidateOptionsMenu()
        }
    }

    // =============================== Setup Drawer Toggle & Toolbar Handling ======================
    private fun setupActionBarDrawerToggle(): ActionBarDrawerToggle {
        return ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ).apply {
            drawerLayout.addDrawerListener(this)
            syncState()
        }
    }

    private fun handleLeadingToolbarTitle() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.fragmentHome -> {
                    toolbar.title = "Have A Nice Day"
                    toolbar.subtitle = Firebase.auth.currentUser?.displayName
                }
                R.id.fragmentFavorite, R.id.fragmentSearch,R.id.fragmentWeeklyPlans, R.id.fragmentSetting -> {
                    toolbar.title = when (destination.id) {
                        R.id.fragmentFavorite -> "Favorites"
                        R.id.fragmentSearch -> "Search"
                        R.id.fragmentWeeklyPlans -> "Plans"
                        else -> "Settings"
                    }
                    toolbar.subtitle = ""
                }
                R.id.addMealFragment -> {
                    toolbar.title = "Add Meal"
                    toolbar.subtitle = ""
                }
                 R.id.mealDatailsFragment -> {
                    toolbar.title = "Meal Details"
                    toolbar.subtitle = ""
                } R.id.filteredMealsByCategoryFragment -> {
                    toolbar.title = "Filtered Meals"
                    toolbar.subtitle = ""
                } R.id.filteredMealsByAreaFragment -> {
                    toolbar.title = "Filtered Meals"
                    toolbar.subtitle = ""
                } R.id.personalInformationFragment -> {
                    toolbar.title = "Personal Information"
                    toolbar.subtitle = ""
                } R.id.aboutUsFragment -> {
                    toolbar.title = "About Us"
                    toolbar.subtitle = ""
                }
                else -> {
                    toolbar.title = "FoodDay"
                    toolbar.subtitle = ""
                }
            }
        }
    }

    private fun handleLeadingToolbarIcon() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isHomeFragment = destination.id == R.id.fragmentHome
            val actionBar = supportActionBar

            if (isHomeFragment) {
                toggle.isDrawerIndicatorEnabled = true
                actionBar?.setDisplayHomeAsUpEnabled(false)
                actionBar?.setHomeAsUpIndicator(R.drawable.menu_icon)
                toggle.setToolbarNavigationClickListener(null)
            } else {
                toggle.isDrawerIndicatorEnabled = false
                actionBar?.setDisplayHomeAsUpEnabled(true)
                actionBar?.setHomeAsUpIndicator(R.drawable.arrow_back_icon)
                toggle.setToolbarNavigationClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
            }

            invalidateOptionsMenu()
        }
    }

    // =============================== Handle Drawer Menu List Clicks ==============================
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val menuItemProfile = menu?.findItem(R.id.profile_image)
        menuItemProfile?.isVisible = navController.currentDestination?.id == R.id.fragmentHome
        return true
    }
}
