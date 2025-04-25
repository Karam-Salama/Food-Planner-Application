package com.example.foodplannerapplication.modules.settings.view
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.helpers.DialogHelper
import com.example.foodplannerapplication.modules.auth.ViewModels.LoginViewModel
import com.example.foodplannerapplication.modules.auth.ViewModels.LoginViewModelFactory
import com.example.foodplannerapplication.modules.auth.models.AuthRepository
import com.example.foodplannerapplication.modules.settings.models.SettingRepo
import com.example.foodplannerapplication.modules.settings.viewmodel.SettingViewModel
import com.example.foodplannerapplication.modules.settings.viewmodel.SettingViewModelFactory
import com.google.firebase.auth.FirebaseUser

class FragmentSetting : Fragment() {
    private lateinit var tvNameProfile: TextView
    private lateinit var tvEmailProfile: TextView
    private lateinit var clPersonItem: ConstraintLayout
    private lateinit var clPlansItem: ConstraintLayout
    private lateinit var clFavoritesItem: ConstraintLayout
    private lateinit var clHelpingItem: ConstraintLayout
    private lateinit var clLogoutItem: ConstraintLayout

    private lateinit var  settingViewModel: SettingViewModel

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupViewModel()
        setupObservers()
        setUpListeners()
    }

    private fun initViews(view: View) {
        tvNameProfile = view.findViewById(R.id.tv_nameProfile)
        tvEmailProfile = view.findViewById(R.id.tv_emailProfile)
        clPersonItem = view.findViewById(R.id.cl_personItem)
        clPlansItem = view.findViewById(R.id.cl_plansItem)
        clFavoritesItem = view.findViewById(R.id.cl_favoriteItem)
        clHelpingItem = view.findViewById(R.id.cl_helpingItem)
        clLogoutItem = view.findViewById(R.id.cl_logoutItem)
    }

    private fun setupViewModel() {
        val repository = SettingRepo()
        settingViewModel = ViewModelProvider(this, SettingViewModelFactory(repository)).get(SettingViewModel::class.java)
    }

    private fun setupObservers() {
        settingViewModel.userData.observe(viewLifecycleOwner, Observer { user ->
            if (settingViewModel.isGuestUser() && user == null) {
                showGuestUI()
            } else {
                showUserUI(user)
            }
        })
    }

    private fun showGuestUI() {
        tvNameProfile.text = "Guest User"
        tvEmailProfile.text = "Guest@user.com"
        clLogoutItem.visibility = View.GONE
        clPersonItem.visibility = View.GONE
    }

    private fun showUserUI(user: FirebaseUser?) {
        tvNameProfile.text = user?.displayName ?: "No Name"
        tvEmailProfile.text = user?.email ?: "No Email"
        clLogoutItem.visibility = View.VISIBLE
        clPersonItem.visibility = View.VISIBLE
    }

    private fun setUpListeners() {
        clPersonItem.setOnClickListener { navigateToPersonalInfo() }
        clPlansItem.setOnClickListener { navigateToPlans() }
        clFavoritesItem.setOnClickListener { navigateToFavorites() }
        clHelpingItem.setOnClickListener { navigateToHelping() }
        clLogoutItem.setOnClickListener { showLogoutDialog() }
    }

    private fun navigateToPersonalInfo() {
        findNavController().navigate(
            FragmentSettingDirections.actionFragmentSettingToPersonalInformationFragment()
        )
    }

    private fun navigateToPlans() {
        findNavController().navigate(
            FragmentSettingDirections.actionFragmentSettingToFragmentWeeklyPlans()
        )
    }

    private fun navigateToFavorites() {
        findNavController().navigate(
            FragmentSettingDirections.actionFragmentSettingToFragmentFavorite()
        )
    }

    private fun navigateToHelping() {
        findNavController().navigate(
            FragmentSettingDirections.actionFragmentSettingToAboutUsFragment()
        )
    }

    private fun showLogoutDialog() {
        DialogHelper.showLogoutDialog(requireContext()) {
            settingViewModel.logout()
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(
            FragmentSettingDirections.actionFragmentSettingToLoginActivity()
        )
    }
}