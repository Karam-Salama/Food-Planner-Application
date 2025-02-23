package com.example.foodplannerapplication.modules.home.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.modules.home.data.server.services.RetrofitHelper
import com.google.android.material.imageview.ShapeableImageView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RandomMealDetailFragment : Fragment() {

    private val randomMealDetailFragmentArgs: RandomMealDetailFragmentArgs by navArgs()

    // UI components
    private lateinit var mealTitle: TextView
    private lateinit var mealImage: ShapeableImageView
    private lateinit var mealCategory: TextView
    private lateinit var mealArea: TextView
    private lateinit var mealInstructions: TextView
    private lateinit var youtubePlayerView: YouTubePlayerView
    private lateinit var mealSource: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_random_meal_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        getMealDetailsByIdFromApi(randomMealDetailFragmentArgs.mealId)
    }

    private fun initViews(view: View) {
        mealTitle = view.findViewById(R.id.tv_mealName)
        mealImage = view.findViewById(R.id.si_mealImage)
        mealCategory = view.findViewById(R.id.tv_mealCategory)
        mealArea = view.findViewById(R.id.tv_mealArea)
    }

    // =============== getMealDetailsByIdFromApi ==========================
    private fun getMealDetailsByIdFromApi(id: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitHelper.retrofitService.getMealById(id)
                val mealOfTheDayDetails = response.meals?.firstOrNull()
                withContext(Dispatchers.Main) {
                    mealTitle.text = mealOfTheDayDetails?.strMeal
                    mealOfTheDayDetails?.strMealThumb?.let { loadMealImage(it) }
                    mealCategory.text = mealOfTheDayDetails?.strCategory
                    mealArea.text = mealOfTheDayDetails?.strArea
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("===>", "Error Fetching Meal Details", e)
                }
            }
        }
    }

    private fun loadMealImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_ic)
            .into(mealImage)
    }
}