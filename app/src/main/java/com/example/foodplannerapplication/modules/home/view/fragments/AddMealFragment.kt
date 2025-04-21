package com.example.foodplannerapplication.modules.home.view.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.utils.helpers.DateUtils
import com.example.foodplannerapplication.core.utils.helpers.MealImageHandler
import com.example.foodplannerapplication.core.utils.helpers.MealValidator
import com.example.foodplannerapplication.core.utils.notifications.MealReminderWorker
import com.example.foodplannerapplication.modules.home.model.cache.database.AddMealDatabase
import com.example.foodplannerapplication.modules.home.model.cache.entity.AddMealModel
import com.example.foodplannerapplication.modules.home.viewmodel.AddMealViewModel
import com.example.foodplannerapplication.modules.home.viewmodel.MyFactory
import com.google.android.material.snackbar.Snackbar
import java.util.*
import java.util.concurrent.TimeUnit

class AddMealFragment : Fragment() {
    private lateinit var addMealViewModel: AddMealViewModel
    private lateinit var mealImageHandler: MealImageHandler
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    private lateinit var ivMealImage: ImageView
    private lateinit var edtMealName: EditText
    private lateinit var mealCategorySpinner: Spinner
    private lateinit var edtMealDate: EditText
    private lateinit var edtMealTime: EditText
    private lateinit var btnSaveMeal: Button

    companion object {
        private const val CALENDAR_PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        edtMealDate.setOnClickListener {
            showDatePicker()
        }

        edtMealTime.setOnClickListener {
            showTimePicker()
        }

        btnSaveMeal.setOnClickListener {
            saveMeal()
        }
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
        addMealViewModel.message.observe(viewLifecycleOwner) { message ->
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

        // للتأكد من القيمة
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
            addMealViewModel.addPlan(mealPlan)
            scheduleMealNotification(dateInMillis, mealName)
            Snackbar.make(requireView(), "Meal added successfully", Snackbar.LENGTH_SHORT).show()

            // استخدم النية الضمنية بدلاً من الإضافة المباشرة
            addToDeviceCalendar(mealName, dateInMillis)
        } else {
            Snackbar.make(requireView(), "Invalid meal data", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun addToDeviceCalendar(title: String, startMillis: Long) {
        val endMillis = startMillis + TimeUnit.HOURS.toMillis(1)

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.Events.DESCRIPTION, "Meal Reminder")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No calendar app found", Toast.LENGTH_SHORT).show()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // الصلاحية ممنوحة، يمكنك متابعة إضافة الحدث
                Toast.makeText(requireContext(), "Calendar permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Calendar permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}