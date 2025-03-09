package com.example.foodplannerapplication.modules.home.view.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
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

class AddMealFragment : Fragment() {
    private lateinit var addMealViewModel: AddMealViewModel
    private lateinit var dateTimePickerHelper: MealDateTimePickerHelper
    private lateinit var mealImageHandler: MealImageHandler
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private lateinit var ivMealImage: ImageView
    private lateinit var edtMealName: EditText
    private lateinit var mealCategorySpinner: Spinner
    private lateinit var edtMealDate: EditText
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
        mealCategorySpinner = view.findViewById(R.id.spinner_meal_type)
        edtMealDate = view.findViewById(R.id.edt_meal_date)
        btnSaveMeal = view.findViewById(R.id.btn_save_meal)
    }

    private fun setUpListeners() {
        ivMealImage.setOnClickListener { openGallery() }
        edtMealDate.setOnClickListener { dateTimePickerHelper.showDatePicker(edtMealDate) }
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
        val selectedCategory = mealCategorySpinner.selectedItem.toString()
        val selectedDate = edtMealDate.text.toString().trim()

        if (mealName.isEmpty() || selectedDate.isEmpty()) {
            Snackbar.make(requireView(), "Please fill all required fields", Snackbar.LENGTH_SHORT).show()
            return
        }

        val imagePath = mealImageHandler.getImagePath(ivMealImage)
        val dateInMillis = DateUtils.convertDateToLong(selectedDate)

        val mealPlan = AddMealModel(
            thumbMealPlan = imagePath,
            nameMealPlan = mealName,
            categoryMealPlan = selectedCategory,
            dateMealPlan = dateInMillis,
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
