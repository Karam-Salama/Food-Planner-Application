package com.example.foodplannerapplication.modules.home.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.functions.CountryFlagMapper
import com.example.foodplannerapplication.modules.home.data.model.AreaModel

class AreaAdapter(private var areas: List<AreaModel?>?,
                  private val context: Context,
                  private val onAreaClick: (String?) -> Unit)
    : RecyclerView.Adapter<AreaAdapter.AreaViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AreaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.area_list_item, parent, false)
        return AreaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AreaViewHolder, position: Int) {
        val currentItem = areas?.get(position)
        if (currentItem != null) {
            holder.areaTitle.text = currentItem.strArea
            holder.areaFlag.text = CountryFlagMapper.getFlagEmoji(currentItem.strArea)
            Log.d("Glide Debug", "=================== Loading Area Flag URL: ${CountryFlagMapper.getFlagEmoji(currentItem.strArea)} ==================== ")
            holder.itemView.setOnClickListener {
                onAreaClick(currentItem.strArea)
            }
        }
    }

    override fun getItemCount(): Int {
        return areas?.size ?: 0
    }

    fun updateAreas(newAreas: List<AreaModel?>?) {
         areas = newAreas
        notifyDataSetChanged()
    }

    class AreaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val areaFlag = itemView.findViewById<TextView>(R.id.tv_flag)
        val areaTitle = itemView.findViewById<TextView>(R.id.tv_areaTitle)
    }
}