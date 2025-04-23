package com.example.foodplannerapplication.modules.settings.view
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.data.cache.CacheHelper
import com.example.foodplannerapplication.core.utils.Constants
import com.example.foodplannerapplication.core.helpers.DialogHelper
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FragmentSetting : Fragment() {
    private lateinit var tvNameProfile: TextView
    private lateinit var tvEmailProfile: TextView
    private lateinit var clPersonItem:ConstraintLayout
    private lateinit var clPlansItem:ConstraintLayout
    private lateinit var clFavoritesItem:ConstraintLayout
    private lateinit var clHelpingItem:ConstraintLayout
    private lateinit var clLogoutItem:ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setUpViews()
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

    private fun setUpViews() {
        val isAuthSkipClicked = CacheHelper.getBoolean(Constants.OnBording_SKIP_KEY, false)
        val currentUser = Firebase.auth.currentUser

        if (isAuthSkipClicked && currentUser == null) {
            tvNameProfile.text = "Guest User"
            tvEmailProfile.text = "Guest@user.com"

            clLogoutItem.visibility = View.GONE
            clPersonItem.visibility = View.GONE
        } else {
            tvNameProfile.text = currentUser?.displayName ?: "No Name"
            tvEmailProfile.text = currentUser?.email ?: "No Email"

            clLogoutItem.visibility = View.VISIBLE
            clPersonItem.visibility = View.VISIBLE
        }
    }

    private fun setUpListeners() {
        // Go To Personal Information
        clPersonItem.setOnClickListener {
            val actionFragmentSettingToPersonalInformationFragment =
                FragmentSettingDirections.actionFragmentSettingToPersonalInformationFragment()
            findNavController().navigate(actionFragmentSettingToPersonalInformationFragment)
        }

        // Go To Plans
        clPlansItem.setOnClickListener {
            val actionFragmentSettingToPlansFragment =
                FragmentSettingDirections.actionFragmentSettingToFragmentWeeklyPlans()
            findNavController().navigate(actionFragmentSettingToPlansFragment)
        }

        // Go To Favorites
        clFavoritesItem.setOnClickListener {
            val actionFragmentSettingToFavoritesFragment =
                FragmentSettingDirections.actionFragmentSettingToFragmentFavorite()
            findNavController().navigate(actionFragmentSettingToFavoritesFragment)
        }

        // Go To Helping
        clHelpingItem.setOnClickListener {
            val actionFragmentSettingToHelpingFragment =
                FragmentSettingDirections.actionFragmentSettingToAboutUsFragment()
            findNavController().navigate(actionFragmentSettingToHelpingFragment)
        }

        // Go To Logout
        clLogoutItem.setOnClickListener {
            DialogHelper.showLogoutDialog(requireContext()){
                Firebase.auth.signOut()
                val actionFragmentSettingToLoginActivity =
                    FragmentSettingDirections.actionFragmentSettingToLoginActivity()
                findNavController().navigate(actionFragmentSettingToLoginActivity)
            }
        }
    }
}