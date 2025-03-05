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
import com.google.android.material.imageview.ShapeableImageView

class CategoryAdapter(
    private var categories: List<CategoryModel?>?,
    private val context: Context,
    private val onCategoryClick: (String?) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = categories?.get(position)
        if (currentItem != null) {
            holder.categoryTitle.text = currentItem.strCategory
            Glide.with(context).load(currentItem.strCategoryThumb).into(holder.categoryImage)

            holder.itemView.setOnClickListener {
                onCategoryClick(currentItem.strCategory)
            }
        }
    }

    override fun getItemCount(): Int {
        return categories?.size ?: 0
    }

    fun updateCategories(newCategories: List<CategoryModel?>?) {
        categories = newCategories
        notifyDataSetChanged()
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val categoryImage = itemView.findViewById<ShapeableImageView>(R.id.itemImage)
        val categoryTitle = itemView.findViewById<TextView>(R.id.itemTitle)
    }
}