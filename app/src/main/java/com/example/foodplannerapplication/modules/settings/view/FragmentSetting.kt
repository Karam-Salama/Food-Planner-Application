package com.example.foodplannerapplication.modules.settings.view
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.foodplannerapplication.R
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
        // Inflate the layout for this fragment
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

    private fun setUpViews(){
        tvNameProfile.text = Firebase.auth.currentUser?.displayName
        tvEmailProfile.text = Firebase.auth.currentUser?.email
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
            showLogoutDialog()
        }
    }

    @SuppressLint("ResourceType")
    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_logout, null)

        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)

        val dialog = builder.setView(dialogView).create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            Firebase.auth.signOut()
            val actionFragmentSettingToLoginActivity =
                FragmentSettingDirections.actionFragmentSettingToLoginActivity()
            findNavController().navigate(actionFragmentSettingToLoginActivity)
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

}