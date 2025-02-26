package com.example.foodplannerapplication.modules.home.view.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.model.FilteredMealModel
import com.example.foodplannerapplication.core.model.ICommonFilteredMealListener
import com.example.foodplannerapplication.core.model.cache.room.database.FavoritesDatabase
import com.example.foodplannerapplication.core.utils.functions.CountryFlagMapper
import com.example.foodplannerapplication.core.viewmodel.AddToFavoriteViewModel
import com.example.foodplannerapplication.core.viewmodel.MyFactory
import com.example.foodplannerapplication.modules.home.model.server.models.MealModel
import com.example.foodplannerapplication.modules.home.model.server.services.RetrofitHelper
import com.example.foodplannerapplication.modules.home.view.adapters.AreaAdapter
import com.example.foodplannerapplication.modules.home.view.adapters.IngredientsAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MealDatailsFragment : Fragment() , ICommonFilteredMealListener{
    // arguments
    private val MealDatailsFragmentArgs: MealDatailsFragmentArgs by navArgs()

    // view model
    private lateinit var addToFavoriteViewModel: AddToFavoriteViewModel

    // ui components
    private lateinit var mealImage: ShapeableImageView
    private lateinit var mealTitle: TextView
    private lateinit var mealCategory: TextView
    private lateinit var mealInstructions: TextView
    private lateinit var youtubePlayerView: YouTubePlayerView
    private lateinit var rcMealIngredients: RecyclerView
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var mealSource: TextView
    private lateinit var favoriteButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_meal_datails, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setUpViewModel()
        setUpRecyclerView(view)
        extractedDataFromViewModel()
        observeMealDetails()

    }

    private fun initViews(view: View) {
        mealImage = view.findViewById(R.id.si_mealImage)
        mealTitle = view.findViewById(R.id.tv_mealName)
        mealCategory = view.findViewById(R.id.tv_mealCategory)
        mealInstructions = view.findViewById(R.id.tv_mealInstructions)
        youtubePlayerView = view.findViewById(R.id.youtubePlayerView)
        mealSource = view.findViewById(R.id.tv_txtNeedToKnowMore)
        favoriteButton = view.findViewById(R.id.saveBtn)
    }

    private fun setUpViewModel() {
        var dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        var myFactory = MyFactory(dao, RetrofitHelper)
        addToFavoriteViewModel = ViewModelProvider(this, myFactory).get(AddToFavoriteViewModel::class.java)
    }

    private fun extractedDataFromViewModel() {
        lifecycleScope.launch {
            addToFavoriteViewModel.fetchMealDetailsById(MealDatailsFragmentArgs.mealId)
        }
    }

    private fun setUpRecyclerView(view: View){
        rcMealIngredients = view.findViewById(R.id.rc_Ingredients)
        ingredientsAdapter = IngredientsAdapter(listOf(), requireContext())
        rcMealIngredients.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = ingredientsAdapter
        }
    }

    private fun observeMealDetails() {
        addToFavoriteViewModel.mealDetails.observe(viewLifecycleOwner) { meal ->
            meal?.let {
                it.strMealThumb?.let { loadMealImage(it) }
                mealTitle.text = it.strMeal
                mealCategory.text = formatCategoryAppearence(it)
                mealInstructions.text = it.strInstructions
                loadYouTubeVideo(it)
                showIngredients(it)
                setupMealSourceLink(it)
            }
        }
    }

    private fun formatCategoryAppearence(filteredMeals: MealModel?) =
        "${filteredMeals?.strArea} ${filteredMeals?.strCategory} ${
            CountryFlagMapper.getFlagEmoji(
                filteredMeals?.strArea
            )
        }"

    private fun loadMealImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder_ic)
            .into(mealImage)
    }

    private fun loadYouTubeVideo(filteredMeals: MealModel?) {
        val videoId = filteredMeals?.strYoutube?.let { extractYoutubeVideoId(it) }
        videoId?.let {
            youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    // Load video but do not autoplay for better UX
                    youTubePlayer.cueVideo(it, 0f)
                }
            })
        }
    }

    private fun extractYoutubeVideoId(url: String): String? {
        val regex =
            "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|%2Fv%2F)[^#\\&\\?\\n]*".toRegex()
        return regex.find(url)?.value
    }

    private fun showIngredients(filteredMeals: MealModel?) {
        filteredMeals?.let { meal ->
            val ingredientNames = mutableListOf<String>()

            for (i in 1..9) {
                val ingredient = meal.javaClass.getDeclaredField("strIngredient$i")
                    .apply { isAccessible = true }
                    .get(meal) as? String

                if (!ingredient.isNullOrEmpty()) {
                    ingredientNames.add(ingredient)
                }
            }

            ingredientsAdapter.updateIngredients(ingredientNames)
        }
    }

    private fun setupMealSourceLink(filteredMeals: MealModel?) {
        mealSource.setOnClickListener {
            filteredMeals?.strSource?.let { url ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) // Open the link in a browser
            }
        }
    }

    override fun onFilteredMealsFavoriteClick(filteredMealsModel: FilteredMealModel?) {
        lifecycleScope.launch {
            addToFavoriteViewModel.saveFilteredMeals(filteredMealsModel)
        }
    }

    override fun onFilteredMealsClick(mealId: String?) {
        TODO("Not yet implemented")
    }
}