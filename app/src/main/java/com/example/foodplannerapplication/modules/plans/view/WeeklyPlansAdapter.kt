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
import com.example.foodplannerapplication.core.helpers.DateUtils
import com.example.foodplannerapplication.modules.plans.models.AddMealModel
import com.google.android.material.imageview.ShapeableImageView

class WeeklyPlansAdapter(
    var weeklyMeals : List<AddMealModel?>?,
    private val context: Context,
    var listener: IWeeklyPlansListener
) : RecyclerView.Adapter<WeeklyPlansAdapter.WeeklyPlansViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyPlansViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.weekly_plans_item, parent, false)
        return WeeklyPlansViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WeeklyPlansViewHolder, position: Int) {
        val currentItem = weeklyMeals?.get(position)
        if (currentItem != null) {
            Glide.with(context).load(currentItem.thumbMealPlan).into(holder.mealImage)
            holder.mealTitle.text = currentItem.nameMealPlan
            holder.mealCategory.text = currentItem.categoryMealPlan
            holder.mealDate.text = DateUtils.convertLongToFormattedDateTime(currentItem.dateMealPlan)
            holder.ivDelete.setOnClickListener {
                listener.onDeleteWeeklyPlansClick(currentItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return weeklyMeals?.size ?: 0
    }

    class WeeklyPlansViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealImage: ShapeableImageView = itemView.findViewById(R.id.iv_mealImage)
        val mealTitle: TextView = itemView.findViewById(R.id.tv_mealName)
        val mealCategory: TextView = itemView.findViewById(R.id.tv_mealCategory)
        val mealDate : TextView = itemView.findViewById(R.id.tv_mealDate)
        val ivDelete : ImageView = itemView.findViewById(R.id.iv_trashIcon)
    }
}
