package com.example.foodplannerapplication.modules.home.view.fragments
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.helpers.DateUtils
import com.example.foodplannerapplication.core.helpers.MealImageHandler
import com.example.foodplannerapplication.core.helpers.MealValidator
import com.example.foodplannerapplication.core.notifications.MealReminderWorker
import com.example.foodplannerapplication.modules.plans.models.AddMealModel
import com.example.foodplannerapplication.modules.plans.models.AddMealToPlansDatabase
import com.example.foodplannerapplication.modules.plans.viewmodel.AddMealToPlansViewModel
import com.example.foodplannerapplication.modules.plans.viewmodel.AddMealToPlansViewModelFactory
import com.google.android.material.snackbar.Snackbar
import java.util.*
import java.util.concurrent.TimeUnit

class AddMealFragment : Fragment() {
    private lateinit var addMealToPlansViewModel: AddMealToPlansViewModel
    private lateinit var mealImageHandler: MealImageHandler
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var ivMealImage: ImageView
    private lateinit var edtMealName: EditText
    private lateinit var mealCategorySpinner: Spinner
    private lateinit var edtMealDate: EditText
    private lateinit var edtMealTime: EditText
    private lateinit var btnSaveMeal: Button

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_add_meal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        edtMealTime = view.findViewById(R.id.edt_meal_time)
        btnSaveMeal = view.findViewById(R.id.btn_save_meal)
    }

    private fun setUpListeners() {
        ivMealImage.setOnClickListener { openGallery() }
        edtMealDate.setOnClickListener {showDatePicker()}
        edtMealTime.setOnClickListener {showTimePicker()}
        btnSaveMeal.setOnClickListener {saveMeal()}
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                edtMealDate.setText(DateUtils.formatDate(selectedDate.timeInMillis))
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }
                edtMealTime.setText(DateUtils.formatTime(selectedTime.timeInMillis))
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun setUpMealCategorySpinner() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.meal_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mealCategorySpinner.adapter = adapter
    }

    private fun observeViewModel() {
        addMealToPlansViewModel.message.observe(viewLifecycleOwner) { message ->
            Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun saveMeal() {
        val mealName = edtMealName.text.toString().trim()
        val selectedCategory = mealCategorySpinner.selectedItem.toString()
        val selectedDate = edtMealDate.text.toString().trim()
        val selectedTime = edtMealTime.text.toString().trim()

        if (mealName.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Snackbar.make(requireView(), "Please fill all required fields", Snackbar.LENGTH_SHORT).show()
            return
        }

        val dateTimeStr = "$selectedDate $selectedTime"
        val dateInMillis = DateUtils.convertDateTimeToLong(dateTimeStr)

        Log.d("AddMealFragment", "Date in millis: $dateInMillis")
        Toast.makeText(requireContext(), "Date: ${Date(dateInMillis)}", Toast.LENGTH_SHORT).show()

        val imagePath = mealImageHandler.getImagePath(ivMealImage)
        val mealPlan = AddMealModel(
            thumbMealPlan = imagePath,
            nameMealPlan = mealName,
            categoryMealPlan = selectedCategory,
            dateMealPlan = dateInMillis,
        )

        if (MealValidator.isValid(mealPlan)) {
            addMealToPlansViewModel.addPlan(mealPlan)
            scheduleMealNotification(dateInMillis, mealName)
            Snackbar.make(requireView(), "Meal added successfully", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(requireView(), "Invalid meal data", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun scheduleMealNotification(mealTimeMillis: Long, mealName: String) {
        val delay = mealTimeMillis - System.currentTimeMillis()

        if (delay > 0) {
            val workRequest = OneTimeWorkRequestBuilder<MealReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(workDataOf(
                    "meal_time" to mealTimeMillis,
                    "meal_name" to mealName
                ))
                .build()

            WorkManager.getInstance(requireContext()).enqueue(workRequest)
        }
    }

    private fun setUpViewModel() {
        val dao = AddMealToPlansDatabase.getDatabase(requireContext()).getAddMealToPlansDao()
        val factory = AddMealToPlansViewModelFactory(dao)
        addMealToPlansViewModel = ViewModelProvider(this, factory).get(AddMealToPlansViewModel::class.java)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun setUpImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
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