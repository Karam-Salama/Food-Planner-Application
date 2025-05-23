package com.example.foodplannerapplication.modules.splash.view
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.foodplannerapplication.modules.onboarding.view.OnboardingActivity
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.data.cache.CacheHelper
import com.example.foodplannerapplication.core.functions.hideStatusAndNavBar.hideStatusAndNavBar
import com.example.foodplannerapplication.modules.auth.view.LoginActivity
import com.example.foodplannerapplication.modules.home.HomeActivity
import com.example.foodplannerapplication.modules.splash.model.SplashRepository
import com.example.foodplannerapplication.modules.splash.viewmodel.SplashViewModel
import com.example.foodplannerapplication.modules.splash.viewmodel.SplashViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        hideStatusAndNavBar()
        CacheHelper.init(this)
        setUpViewModel()
        observeViewModel()
        viewModel.determineDestination()
    }

    private fun setUpViewModel() {
        val repository = SplashRepository(CacheHelper)
        val factory = SplashViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(SplashViewModel::class.java)
    }

    private fun observeViewModel() {
        viewModel.navigationEvent.observe(this) { destination ->
            when (destination) {
                SplashViewModel.NavigationDestination.Onboarding -> navigateTo(OnboardingActivity::class.java)
                SplashViewModel.NavigationDestination.Login -> navigateTo(LoginActivity::class.java)
                SplashViewModel.NavigationDestination.Home -> navigateTo(HomeActivity::class.java)
                else -> {}
            }
        }
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
        finish()
    }
}

