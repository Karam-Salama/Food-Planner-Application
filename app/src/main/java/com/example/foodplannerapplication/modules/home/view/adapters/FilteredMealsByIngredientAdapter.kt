package com.example.foodplannerapplication.modules.home.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.model.FilteredMealModel
import com.example.foodplannerapplication.core.model.ICommonFilteredMealListener
import com.example.foodplannerapplication.modules.home.view.adapters.FilteredMealsByCategoryAdapter.CategoryViewHolder
import com.google.android.material.imageview.ShapeableImageView

class FilteredMealsByIngredientAdapter(var filteredMeals: List<FilteredMealModel?>?,
                                       private val context: Context,
                                       var listener: ICommonFilteredMealListener
) : RecyclerView.Adapter<FilteredMealsByIngredientAdapter.IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.filtered_meals_by_category_item, parent, false)
        return IngredientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val currentItem = filteredMeals?.get(position)
        if (currentItem != null) {
            Glide.with(context).load(currentItem.strMealThumb).into(holder.mealImage)
            holder.mealTitle.text = currentItem.strMeal

            if (currentItem.isFavorite) {
                holder.mealFavorite.setImageResource(R.drawable.ic_favorite_filled)
                holder.mealFavorite.setColorFilter(ContextCompat.getColor(context, R.color.red))
            } else {
                holder.mealFavorite.setImageResource(R.drawable.ic_favorite_border)
                holder.mealFavorite.setColorFilter(ContextCompat.getColor(context, R.color.black))
            }

            holder.mealFavorite.setOnClickListener {
                currentItem.isFavorite = !currentItem.isFavorite
                listener.onFilteredMealsFavoriteClick(currentItem)
                notifyItemChanged(position)
            }
        }

        holder.itemView.setOnClickListener {
            listener.onFilteredMealsClick(currentItem?.idMeal)
        }
    }

    override fun getItemCount(): Int {
        return filteredMeals?.size ?: 0
    }

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealTitle = itemView.findViewById<TextView>(R.id.tv_mealName)
        val mealImage = itemView.findViewById<ShapeableImageView>(R.id.si_mealImage)
        val mealFavorite: ImageView = itemView.findViewById(R.id.iv_favorite)
    }

}