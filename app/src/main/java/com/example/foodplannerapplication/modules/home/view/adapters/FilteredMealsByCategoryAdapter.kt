package com.example.foodplannerapplication.modules.home.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.home.model.server.models.CategoryModel
import com.example.foodplannerapplication.modules.home.model.server.models.FilteredMealModel
import com.google.android.material.imageview.ShapeableImageView

class FilteredMealsByCategoryAdapter(
    private var filteredMeals: List<FilteredMealModel?>?,
    private val context: Context,
    private val onFilteredMealClick: (String?) -> Unit
) : RecyclerView.Adapter<FilteredMealsByCategoryAdapter.CategoryViewHolder>() {

    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.filtered_meals_by_category_item, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = filteredMeals?.get(position)
        if (currentItem != null) {
            Glide.with(context).load(currentItem.strMealThumb).into(holder.mealImage)
            holder.mealTitle.text = currentItem.strMeal
        }
        holder.itemView.setOnClickListener {
            onFilteredMealClick(currentItem?.idMeal)
        }
    }

    override fun getItemCount(): Int {
        return filteredMeals?.size ?: 0
    }

    fun updateFilteredMeals(newFilteredMeals: List<FilteredMealModel?>?) {
        filteredMeals = newFilteredMeals
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val mealTitle = itemView.findViewById<TextView>(R.id.tv_mealName)
        val mealImage = itemView.findViewById<ShapeableImageView>(R.id.si_mealImage)
    }
}