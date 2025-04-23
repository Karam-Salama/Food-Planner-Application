package com.example.foodplannerapplication.modules.settings.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.functions.Validation
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class PersonalInformationFragment : Fragment() {
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var ivEditName: ImageView
    private lateinit var ivEditEmail: ImageView
    private lateinit var etCurrentPassword: TextInputEditText
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etConfirmNewPassword: TextInputEditText
    private lateinit var btnSaveChanges: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personal_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setUpViews()
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

    private fun setUpViews() {
        val user = Firebase.auth.currentUser

        etName.text = user?.displayName?.let {
            android.text.Editable.Factory.getInstance().newEditable(it)
        }
        etEmail.text = user?.email?.let {
            android.text.Editable.Factory.getInstance().newEditable(it)
        }

        etName.isEnabled = false
        etEmail.isEnabled = false
    }

    private fun setListeners() {
        ivEditName.setOnClickListener { enableEditing(etName) }
        ivEditEmail.setOnClickListener { enableEditing(etEmail) }
        btnSaveChanges.setOnClickListener { saveChanges() }
    }

    private fun enableEditing(editText: TextInputEditText) {
        editText.isEnabled = true
        editText.requestFocus()
    }

    private fun saveChanges() {
        val user = Firebase.auth.currentUser ?: return

        val newName = etName.text.toString().trim()
        val newEmail = etEmail.text.toString().trim()
        val currentPassword = etCurrentPassword.text.toString().trim()
        val newPassword = etNewPassword.text.toString().trim()
        val confirmPassword = etConfirmNewPassword.text.toString().trim()

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // تحديث الاسم
            if (newName.isNotEmpty() && newName != user.displayName) {
                val profileUpdates = userProfileChangeRequest {
                    displayName = newName
                }
                user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Name updated successfully", Toast.LENGTH_SHORT).show()
                        etName.isEnabled = false
                    } else {
                        Toast.makeText(requireContext(), "Failed to update name", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // تحديث البريد الإلكتروني
            if (newEmail.isNotEmpty() && newEmail != user.email) {
                user.updateEmail(newEmail).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Email updated successfully", Toast.LENGTH_SHORT).show()
                        etEmail.isEnabled = false
                    } else {
                        Toast.makeText(requireContext(), "Failed to update email", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // التحقق من صحة كلمات المرور وتغييرها
            val passwordError = Validation.validatePassword(newPassword)
            val confirmPasswordError = Validation.validateConfirmPassword(confirmPassword, newPassword)

            if (newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) {
                if (passwordError != null) {
                    etNewPassword.error = passwordError
                    return
                }

                if (confirmPasswordError != null) {
                    etConfirmNewPassword.error = confirmPasswordError
                    return
                }

                if (currentPassword.isEmpty()) {
                    etCurrentPassword.error = "Current password is required."
                    return
                }

                // تغيير كلمة المرور
                changePassword(currentPassword, newPassword)
            }

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "An error occurred: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = Firebase.auth.currentUser ?: return

        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

        user.reauthenticate(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                        etCurrentPassword.text = null
                        etNewPassword.text = null
                        etConfirmNewPassword.text = null
                    } else {
                        Toast.makeText(requireContext(), "Failed to update password", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                etCurrentPassword.error = "Incorrect current password."
            }
        }
    }
}
