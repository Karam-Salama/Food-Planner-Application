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
import com.example.foodplannerapplication.core.data.models.FilteredMealModel
import com.example.foodplannerapplication.core.data.models.ICommonFilteredMealListener
import com.google.android.material.imageview.ShapeableImageView

class FilteredMealsByAreaAdapter(
    private val context: Context,
    private val isFavoriteScreen: Boolean = false,
    private val listener: ICommonFilteredMealListener
) : RecyclerView.Adapter<FilteredMealsByAreaAdapter.FilteredMealsByAreaViewHolder>() {

    private var mealsList: MutableList<FilteredMealModel> = mutableListOf()

    fun updateList(newList: List<FilteredMealModel>) {
        mealsList.clear()
        mealsList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilteredMealsByAreaViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.filtered_meals_by_category_item, parent, false)
        return FilteredMealsByAreaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FilteredMealsByAreaViewHolder, position: Int) {
        val currentItem = mealsList[position]

        Glide.with(context)
            .load(currentItem.strMealThumb)
            .into(holder.mealImage)

        holder.mealTitle.text = currentItem.strMeal

        if (isFavoriteScreen) {
            holder.mealFavorite.setImageResource(R.drawable.ic_favorite_filled)
            holder.mealFavorite.setColorFilter(ContextCompat.getColor(context, R.color.red))
        } else {
            if (currentItem.isFavorite) {
                holder.mealFavorite.setImageResource(R.drawable.ic_favorite_filled)
                holder.mealFavorite.setColorFilter(ContextCompat.getColor(context, R.color.red))
            } else {
                holder.mealFavorite.setImageResource(R.drawable.ic_favorite_border)
                holder.mealFavorite.setColorFilter(ContextCompat.getColor(context, R.color.black))
            }
        }

        holder.mealFavorite.setOnClickListener {
            if (!isFavoriteScreen) {
                currentItem.isFavorite = !currentItem.isFavorite
                notifyItemChanged(position)
            }
            listener.onFilteredMealsFavoriteClick(currentItem)
        }

        holder.itemView.setOnClickListener {
            listener.onFilteredMealsClick(currentItem.idMeal)
        }
    }

    override fun getItemCount(): Int = mealsList.size

    class FilteredMealsByAreaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealTitle: TextView = itemView.findViewById(R.id.tv_mealName)
        val mealImage: ShapeableImageView = itemView.findViewById(R.id.si_mealImage)
        val mealFavorite: ImageView = itemView.findViewById(R.id.iv_favorite)
    }
}