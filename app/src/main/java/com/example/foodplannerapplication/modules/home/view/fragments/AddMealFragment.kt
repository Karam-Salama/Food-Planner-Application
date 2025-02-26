package com.example.foodplannerapplication.modules.home.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.utils.helpers.DateUtils
import com.example.foodplannerapplication.core.utils.helpers.MealDateTimePickerHelper
import com.example.foodplannerapplication.core.utils.helpers.MealImageHandler
import com.example.foodplannerapplication.core.utils.helpers.MealValidator
import com.example.foodplannerapplication.modules.home.model.cache.database.AddMealDatabase
import com.example.foodplannerapplication.modules.home.model.cache.entity.AddMealModel
import com.example.foodplannerapplication.modules.home.viewmodel.AddMealViewModel
import com.example.foodplannerapplication.modules.home.viewmodel.MyFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
class AddMealFragment : Fragment() {
    private lateinit var addMealViewModel: AddMealViewModel
    private lateinit var dateTimePickerHelper: MealDateTimePickerHelper
    private lateinit var mealImageHandler: MealImageHandler
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private lateinit var ivMealImage: ImageView
    private lateinit var edtMealName: EditText
    private lateinit var edtMealCalories: EditText
    private lateinit var mealCategorySpinner: Spinner
    private lateinit var edtMealDate: EditText
    private lateinit var edtMealTime: EditText
    private lateinit var edtMealDescription: EditText
    private lateinit var btnSaveMeal: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dateTimePickerHelper = MealDateTimePickerHelper(requireContext())
        mealImageHandler = MealImageHandler(requireContext())
        setUpViewModel()
        initViews(view)
        setUpListeners()
        setUpMealCategorySpinner()
        observeViewModel()
        setUpImagePicker()
    }

    private fun initViews(view: View) {
        ivMealImage = view.findViewById(R.id.iv_meal_image)
        edtMealName = view.findViewById(R.id.edt_meal_name)
        edtMealCalories = view.findViewById(R.id.edt_meal_calories)
        mealCategorySpinner = view.findViewById(R.id.spinner_meal_type)
        edtMealDate = view.findViewById(R.id.edt_meal_date)
        edtMealTime = view.findViewById(R.id.edt_meal_time)
        edtMealDescription = view.findViewById(R.id.edt_meal_description)
        btnSaveMeal = view.findViewById(R.id.btn_save_meal)
    }

    private fun setUpListeners() {
        ivMealImage.setOnClickListener { openGallery() }
        edtMealDate.setOnClickListener { dateTimePickerHelper.showDatePicker(edtMealDate) }
        edtMealTime.setOnClickListener { dateTimePickerHelper.showTimePicker(edtMealTime) }
        btnSaveMeal.setOnClickListener { saveMeal() }
    }

    private fun setUpMealCategorySpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.meal_types, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mealCategorySpinner.adapter = adapter
    }

    private fun observeViewModel() {
        addMealViewModel.message.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun saveMeal() {
        val mealName = edtMealName.text.toString().trim()
        val mealCalories = edtMealCalories.text.toString().trim().toIntOrNull() ?: 0
        val selectedCategory = mealCategorySpinner.selectedItem.toString()
        val selectedDate = edtMealDate.text.toString().trim()
        val selectedTime = edtMealTime.text.toString().trim()
        val mealDescription = edtMealDescription.text.toString().trim()

        if (mealName.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Snackbar.make(requireView(), "Please fill all required fields", Snackbar.LENGTH_SHORT).show()
            return
        }

        val imagePath = mealImageHandler.getImagePath(ivMealImage)
        val dateInMillis = DateUtils.convertDateToLong(selectedDate)

        val mealPlan = AddMealModel(
            thumbMealPlan = imagePath,
            nameMealPlan = mealName,
            caloriesMealPlan = mealCalories,
            categoryMealPlan = selectedCategory,
            dateMealPlan = dateInMillis,
            timeMealPlan = selectedTime,
            descriptionMealPlan = mealDescription
        )

        if (MealValidator.isValid(mealPlan)) {
            addMealViewModel.addPlan(mealPlan)
        } else {
            Snackbar.make(requireView(), "Invalid Meal Data", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setUpViewModel() {
        val dao = AddMealDatabase.getDatabase(requireContext()).getAddMealDao()
        val factory = MyFactory(dao)
        addMealViewModel = ViewModelProvider(this, factory).get(AddMealViewModel::class.java)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun setUpImagePicker() {
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { imageUri ->
                    Glide.with(this)
                        .load(imageUri)
                        .into(ivMealImage)
                }
            }
        }
    }
}
