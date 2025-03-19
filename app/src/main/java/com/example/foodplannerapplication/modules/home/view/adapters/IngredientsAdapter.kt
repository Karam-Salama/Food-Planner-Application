package com.example.foodplannerapplication.modules.home.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.utils.classes.Constants
import com.google.android.material.imageview.ShapeableImageView

class IngredientsAdapter(private var ingredients: List<String>, private val context: Context,
                         private val onIngredientClick: (String?) -> Unit) : RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_list_item, parent, false)
        return IngredientsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        val currentItem = ingredients[position]
        if (currentItem != null) {
            holder.ingredientTitle.text = currentItem
            Glide.with(holder.itemView.context)
                .load("${Constants.INGREDIENTS_IMAGES_URL}${currentItem}.png")
                .placeholder(R.drawable.placeholder_ic)
                .into(holder.ingredientImage)
            holder.itemView.setOnClickListener { onIngredientClick(currentItem) }
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    fun updateIngredients(newIngredients: List<String>) {
        ingredients = newIngredients
        notifyDataSetChanged()
    }

    class IngredientsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ingredientImage = itemView.findViewById<ShapeableImageView>(R.id.itemImage)
        val ingredientTitle = itemView.findViewById<TextView>(R.id.itemTitle)
    }
}