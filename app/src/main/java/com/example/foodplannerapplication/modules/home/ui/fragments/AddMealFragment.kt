package com.example.foodplannerapplication.modules.home.ui.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
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
import com.example.foodplannerapplication.R
import java.util.Calendar

class AddMealFragment : Fragment() {
    private lateinit var ivMealImage: ImageView
    private lateinit var edtMealName: EditText
    private lateinit var edtMealCalories: EditText
    private lateinit var mealCategorySpinner: Spinner
    private lateinit var edtMealDate: EditText
    private lateinit var edtMealTime: EditText
    private lateinit var edtMealDescription: EditText
    private lateinit var btnSaveMeal: Button

    private val PICK_IMAGE_REQUEST = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_meal, container, false)

        ivMealImage = view.findViewById(R.id.iv_meal_image)
        edtMealDate = view.findViewById(R.id.edt_meal_date)
        edtMealTime = view.findViewById(R.id.edt_meal_time)
        btnSaveMeal = view.findViewById(R.id.btn_save_meal)
        edtMealName = view.findViewById(R.id.edt_meal_name)
        edtMealCalories = view.findViewById(R.id.edt_meal_calories)
        edtMealDescription = view.findViewById(R.id.edt_meal_description)

        ivMealImage.setOnClickListener { openGallery() }
        edtMealDate.setOnClickListener { showDatePickerDialog() }
        edtMealTime.setOnClickListener { showTimePickerDialog() }
        btnSaveMeal.setOnClickListener { saveMeal() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mealCategorySpinner = view.findViewById(R.id.spinner_meal_type)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.meal_types,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mealCategorySpinner.adapter = adapter
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            edtMealDate.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            edtMealTime.setText(formattedTime)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            ivMealImage.setImageURI(imageUri)
        }
    }

    private fun saveMeal() {
        val mealName = edtMealName.text.toString().trim()
        val mealCalories = edtMealCalories.text.toString().trim()
        val selectedCategory = mealCategorySpinner.selectedItem.toString()
        val selectedDate = edtMealDate.text.toString().trim()
        val selectedTime = edtMealTime.text.toString().trim()
        val mealDescription = edtMealDescription.text.toString().trim()

        // Logic to save meal details (e.g., store in database)
    }
}
