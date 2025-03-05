package com.example.foodplannerapplication.modules.plans.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.model.ICommonFilteredMealListener
import com.example.foodplannerapplication.core.utils.helpers.DateUtils
import com.example.foodplannerapplication.modules.home.model.cache.entity.AddMealModel
import com.google.android.material.imageview.ShapeableImageView

class WeeklyPlansAdapter(
    var weeklyMeals : List<AddMealModel?>?,
    private val context: Context,
    var listener: ICommonFilteredMealListener) : RecyclerView.Adapter<WeeklyPlansAdapter.WeeklyPlansViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyPlansViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.weekly_plans_item, parent, false)
        return WeeklyPlansViewHolder(itemView)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WeeklyPlansViewHolder, position: Int) {
        val currentItem = weeklyMeals?.get(position)
        if (currentItem != null) {
            Glide.with(context).load(currentItem.thumbMealPlan).into(holder.mealImage)
            holder.mealTitle.text = currentItem.nameMealPlan
            holder.mealCategory.text = currentItem.categoryMealPlan
            holder.mealCalories.text = "${ currentItem.caloriesMealPlan.toString() } Calories"

            val formattedDate = DateUtils.convertLongToDate(currentItem.dateMealPlan)
            holder.mealDate.text = formattedDate

             holder.mealTime.text = currentItem.timeMealPlan
        }

        holder.itemView.setOnClickListener {
            listener.onFilteredMealsClick(currentItem?.idMealPlan.toString())
        }
    }


    override fun getItemCount(): Int {
        return weeklyMeals?.size ?: 0
    }

    class WeeklyPlansViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealImage: ShapeableImageView = itemView.findViewById(R.id.iv_mealImage)
        val mealTitle: TextView = itemView.findViewById(R.id.tv_mealName)
        val mealCalories: TextView = itemView.findViewById(R.id.tv_mealCalories)
        val mealCategory: TextView = itemView.findViewById(R.id.tv_mealCategory)
        val mealDate : TextView = itemView.findViewById(R.id.tv_mealDate)
        val mealTime : TextView = itemView.findViewById(R.id.tv_mealTime)
    }
}
