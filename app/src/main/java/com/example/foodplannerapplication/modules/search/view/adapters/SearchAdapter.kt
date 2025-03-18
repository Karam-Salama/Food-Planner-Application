package com.example.foodplannerapplication.modules.search.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.model.FilteredMealModel
import com.example.foodplannerapplication.core.utils.classes.Constants
import com.example.foodplannerapplication.core.utils.functions.CountryFlagMapper
import com.example.foodplannerapplication.modules.home.model.server.models.AreaModel
import com.example.foodplannerapplication.modules.home.model.server.models.CategoryModel
import com.google.android.material.imageview.ShapeableImageView
import android.util.Log
import com.example.foodplannerapplication.modules.home.model.server.models.IngredientModel
import com.example.foodplannerapplication.modules.search.view.ICommonSearchFilteredListener

class SearchAdapter(
    private val context: Context,
    private val listener: ICommonSearchFilteredListener
) : ListAdapter<Any, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    enum class ItemType { MEAL, INGREDIENT, AREA, CATEGORY }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        Log.d("AdapterDebug", "Item at $position: ${item::class.java.simpleName}")

        return when (item) {
            is FilteredMealModel -> ItemType.MEAL.ordinal
            is IngredientModel -> ItemType.INGREDIENT.ordinal
            is AreaModel -> ItemType.AREA.ordinal
            is CategoryModel -> ItemType.CATEGORY.ordinal
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("AdapterDebug", "Creating ViewHolder for viewType: $viewType")

        val inflater = LayoutInflater.from(parent.context)
        val view = when (viewType) {
            ItemType.MEAL.ordinal -> inflater.inflate(R.layout.search_filtered_item, parent, false)
            ItemType.INGREDIENT.ordinal, ItemType.CATEGORY.ordinal -> inflater.inflate(R.layout.category_list_item, parent, false)
            ItemType.AREA.ordinal -> inflater.inflate(R.layout.area_list_item, parent, false)
            else -> throw IllegalArgumentException("Unknown view type")
        }
        return createViewHolder(view, viewType)
    }

    private fun createViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        ItemType.MEAL.ordinal -> MealViewHolder(view)
        ItemType.INGREDIENT.ordinal -> IngredientViewHolder(view)
        ItemType.AREA.ordinal -> AreaViewHolder(view)
        ItemType.CATEGORY.ordinal -> CategoryViewHolder(view)
        else -> throw IllegalArgumentException("Unknown view type")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MealViewHolder -> bindMeal(holder, getItem(position) as FilteredMealModel)
            is IngredientViewHolder -> bindIngredient(holder, getItem(position) as IngredientModel)
            is AreaViewHolder -> bindArea(holder, getItem(position) as AreaModel)
            is CategoryViewHolder -> bindCategory(holder, getItem(position) as CategoryModel)
        }
    }

    private fun bindMeal(holder: MealViewHolder, item: FilteredMealModel) {
        val imageUrl = item.strMealThumb
        Log.d("Glide Debug", "Loading Meal Image URL: $imageUrl")

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_ic)
                .error(R.drawable.error_ic)
                .into(holder.mealImage)
        } else {
            holder.mealImage.setImageResource(R.drawable.error_ic)
        }

        holder.mealTitle.text = item.strMeal
        holder.itemView.setOnClickListener { listener.onFilteredMealsClick(item.idMeal) }

    }

    private fun bindIngredient(holder: IngredientViewHolder, ingredient: IngredientModel) {
        val imageUrl = "${Constants.INGREDIENTS_IMAGES_URL}${ingredient.strIngredient}.png"
        Log.d("Glide Debug", "Loading Ingredient Image URL: $imageUrl")

        holder.ingredientTitle.text = ingredient.strIngredient
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_ic)
            .into(holder.ingredientImage)

        holder.itemView.setOnClickListener { listener.openMealsActivityByIngredient(ingredient.strIngredient) }
    }

    private fun bindArea(holder: AreaViewHolder, currentItem: AreaModel) {
        if (currentItem != null) {
            holder.areaTitle.text = currentItem.strArea
            holder.areaFlag.text = CountryFlagMapper.getFlagEmoji(currentItem.strArea)
            holder.itemView.setOnClickListener { listener.openMealsActivityByArea(currentItem.strArea) }
        }
    }

    private fun bindCategory(holder: CategoryViewHolder, currentItem: CategoryModel) {
        val imageUrl = currentItem.strCategoryThumb
        Log.d("Glide Debug", "Loading Category Image URL: $imageUrl")

        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_ic)
                .error(R.drawable.error_ic)
                .into(holder.categoryImage)
        } else {
            holder.categoryImage.setImageResource(R.drawable.error_ic)
        }

        holder.categoryTitle.text = currentItem.strCategory
        holder.itemView.setOnClickListener { listener.openMealsActivityByCategory(currentItem.strCategory) }
    }

    fun updateList(newItems: List<Any>) = submitList(newItems)

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mealTitle: TextView = view.findViewById(R.id.tv_mealName)
        val mealImage: ShapeableImageView = view.findViewById(R.id.si_mealImage)
    }

    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ingredientTitle: TextView = view.findViewById(R.id.itemTitle)
        val ingredientImage: ImageView = view.findViewById(R.id.itemImage)
    }

    class AreaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val areaTitle: TextView = view.findViewById(R.id.tv_areaTitle)
        val areaFlag: TextView = view.findViewById(R.id.tv_flag)
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryTitle: TextView = view.findViewById(R.id.itemTitle)
        val categoryImage: ImageView = view.findViewById(R.id.itemImage)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any) = oldItem == newItem

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Any, newItem: Any) = oldItem == newItem
        }
    }
}
