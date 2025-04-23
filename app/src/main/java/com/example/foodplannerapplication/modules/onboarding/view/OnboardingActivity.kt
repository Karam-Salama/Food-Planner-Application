package com.example.foodplannerapplication.modules.onboarding.view

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.data.cache.CacheHelper
import com.example.foodplannerapplication.core.functions.hideStatusAndNavBar.hideStatusAndNavBar
import com.example.foodplannerapplication.modules.auth.view.LoginActivity
import com.example.foodplannerapplication.modules.home.HomeActivity
import com.example.foodplannerapplication.modules.onboarding.model.OnboardingRepository
import com.example.foodplannerapplication.modules.onboarding.viewmodel.OnboardingViewModel
import com.example.foodplannerapplication.modules.onboarding.viewmodel.OnboardingViewModelFactory

class OnboardingActivity : AppCompatActivity() {
    private lateinit var startButton: Button
    private lateinit var rotatedImage: ImageView
    private lateinit var rotateAnimation: Animation
    private lateinit var tvSkip: TextView
    private lateinit var viewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_onboarding)

        hideStatusAndNavBar()
        CacheHelper.init(this)
        setupViews()
        setupAnimations()
        setupViewModel()
        observeNavigationEvents()
        setupClickListeners()
    }

    private fun setupViews() {
        rotatedImage = findViewById(R.id.imageView_dish3)
        startButton = findViewById(R.id.startButton)
        tvSkip = findViewById(R.id.tv_skip)
    }

    private fun setupAnimations() {
        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        rotatedImage.startAnimation(rotateAnimation)
    }

    private fun setupViewModel() {
        val repository = OnboardingRepository(CacheHelper)
        viewModel = ViewModelProvider(this, OnboardingViewModelFactory(repository)
        ).get(OnboardingViewModel::class.java)
    }

    private fun observeNavigationEvents() {
        viewModel.navigationEvent.observe(this) { destination ->
            when (destination) {
                OnboardingViewModel.NavigationDestination.Login -> navigateTo(LoginActivity::class.java)
                OnboardingViewModel.NavigationDestination.Home -> navigateTo(HomeActivity::class.java)
                null -> { /* Do nothing */ }
            }
        }
    }

    private fun setupClickListeners() {
        startButton.setOnClickListener { viewModel.handleStartButtonClick() }
        tvSkip.setOnClickListener { viewModel.handleSkipButtonClick() }
    }

    private fun navigateTo(destination: Class<*>) {
        startActivity(Intent(this, destination))
        finish()
    }
}