package com.example.foodplannerapplication.modules.settings.view
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.functions.Validation
import com.example.foodplannerapplication.modules.settings.models.SettingRepo
import com.example.foodplannerapplication.modules.settings.viewmodel.SettingViewModel
import com.example.foodplannerapplication.modules.settings.viewmodel.SettingViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class PersonalInformationFragment : Fragment() {
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var ivEditName: ImageView
    private lateinit var ivEditEmail: ImageView
    private lateinit var etCurrentPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmNewPassword: TextInputEditText
    private lateinit var btnSaveChanges: Button

    private lateinit var  settingViewModel: SettingViewModel

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_personal_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupViewModel()
        setupObservers()
        setListeners()
    }

    private fun initViews(view: View) {
        etName = view.findViewById(R.id.et_name)
        etEmail = view.findViewById(R.id.et_email)
        ivEditName = view.findViewById(R.id.iv_edit_name)
        ivEditEmail = view.findViewById(R.id.iv_edit_email)
        etCurrentPassword = view.findViewById(R.id.et_current_password)
        etNewPassword = view.findViewById(R.id.et_new_password)
        etConfirmNewPassword = view.findViewById(R.id.et_confirm_password)
        btnSaveChanges = view.findViewById(R.id.btn_save_changes)
    }

    private fun setupViewModel() {
        val repository = SettingRepo()
        settingViewModel = ViewModelProvider(this, SettingViewModelFactory(repository)).get(SettingViewModel::class.java)
    }

    private fun setupObservers() {
        settingViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                etName.setText(it.displayName ?: "")
                etEmail.setText(it.email ?: "")
            }
        }

        settingViewModel.updateResult.observe(viewLifecycleOwner) { (success, message) ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            if (success) {
                etName.isEnabled = false
                etEmail.isEnabled = false
                etCurrentPassword.text?.clear()
                etNewPassword.text?.clear()
                etConfirmNewPassword.text?.clear()
            }
        }
    }

    private fun setListeners() {
        ivEditName.setOnClickListener { etName.isEnabled = true; etName.requestFocus() }
        ivEditEmail.setOnClickListener { etEmail.isEnabled = true; etEmail.requestFocus() }
        btnSaveChanges.setOnClickListener { validateAndSaveChanges() }
    }

    private fun validateAndSaveChanges() {
        val newName = etName.text.toString().trim()
        val newEmail = etEmail.text.toString().trim()
        val currentPassword = etCurrentPassword.text.toString().trim()
        val newPassword = etNewPassword.text.toString().trim()
        val confirmPassword = etConfirmNewPassword.text.toString().trim()

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Update name if changed
        settingViewModel.userData.value?.let { user ->
            if (newName != user.displayName) {
                settingViewModel.updateUserName(newName)
            }
        }

        // Update email if changed
        settingViewModel.userData.value?.let { user ->
            if (newEmail != user.email) {
                settingViewModel.updateUserEmail(newEmail)
            }
        }

        // Handle password change if fields are not empty
        if (newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) {
            val passwordError = Validation.validatePassword(newPassword)
            val confirmPasswordError = Validation.validateConfirmPassword(confirmPassword, newPassword)

            when {
                passwordError != null -> etNewPassword.error = passwordError
                confirmPasswordError != null -> etConfirmNewPassword.error = confirmPasswordError
                currentPassword.isEmpty() -> etCurrentPassword.error = "Current password is required"
                else -> settingViewModel.changePassword(currentPassword, newPassword)
            }
        }
    }
}