package com.example.foodplannerapplication.modules.home.view.fragments
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodplannerapplication.R
import com.example.foodplannerapplication.core.data.models.FilteredMealModel
import com.example.foodplannerapplication.core.data.models.ICommonFilteredMealListener
import com.example.foodplannerapplication.modules.favorite.models.FavoritesDatabase
import com.example.foodplannerapplication.core.functions.CountryFlagMapper
import com.example.foodplannerapplication.core.helpers.MealDateTimePickerHelper
import com.example.foodplannerapplication.core.helpers.MealValidator
import com.example.foodplannerapplication.core.notifications.SchedulerMealNotification
import com.example.foodplannerapplication.modules.plans.models.AddMealModel
import com.example.foodplannerapplication.modules.home.data.model.MealModel
import com.example.foodplannerapplication.core.data.server.retrofit.RetrofitHelper
import com.example.foodplannerapplication.modules.favorite.viewmodel.AddMealToFavoritesViewModel
import com.example.foodplannerapplication.modules.favorite.viewmodel.AddMealToFavoritesViewModelFactory
import com.example.foodplannerapplication.modules.home.view.adapters.IngredientsAdapter
import com.example.foodplannerapplication.modules.plans.models.AddMealToPlansDatabase
import com.example.foodplannerapplication.modules.plans.viewmodel.AddMealToPlansViewModel
import com.example.foodplannerapplication.modules.plans.viewmodel.AddMealToPlansViewModelFactory
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.launch
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class MealDatailsFragment : Fragment() , ICommonFilteredMealListener {
    // arguments
    private val MealDatailsFragmentArgs: MealDatailsFragmentArgs by navArgs()
    // view model
    private lateinit var addToFavoriteViewModel: AddMealToFavoritesViewModel
    private lateinit var addMealToPlansViewModel: AddMealToPlansViewModel
    // helpers
    private lateinit var dateTimePickerHelper: MealDateTimePickerHelper
    // ui components
    private lateinit var mealImage: ShapeableImageView
    private lateinit var mealTitle: TextView
    private lateinit var mealCategory: TextView
    private lateinit var mealCalImgView: ImageView
    private lateinit var mealFavImgView: ImageView
    private lateinit var mealInstructions: TextView
    private lateinit var youtubePlayerView: YouTubePlayerView
    private lateinit var rcMealIngredients: RecyclerView
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var mealSource: TextView
    private  lateinit var btnAddToPlan: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_meal_datails, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateTimePickerHelper = MealDateTimePickerHelper(requireContext())
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
        btnAddToPlan = view.findViewById(R.id.btn_addToPlan)
        mealCalImgView = view.findViewById(R.id.iv_calendar)
        mealFavImgView = view.findViewById(R.id.iv_fav)
    }

    private fun setUpViewModel() {
        var dao = FavoritesDatabase.getDatabase(requireContext()).getFavoritesDao()
        var myFactory = AddMealToFavoritesViewModelFactory(dao, RetrofitHelper)
        addToFavoriteViewModel = ViewModelProvider(this, myFactory).get(AddMealToFavoritesViewModel::class.java)
        val daoAddMeal = AddMealToPlansDatabase.getDatabase(requireContext()).getAddMealToPlansDao()
        val factory = AddMealToPlansViewModelFactory(daoAddMeal)
        addMealToPlansViewModel = ViewModelProvider(this, factory).get(AddMealToPlansViewModel::class.java)
    }

    private fun extractedDataFromViewModel() {
        lifecycleScope.launch {
            addToFavoriteViewModel.fetchMealDetailsById(MealDatailsFragmentArgs.mealId)
        }
    }

    private fun setUpRecyclerView(view: View){
        rcMealIngredients = view.findViewById(R.id.rc_Ingredients)
        ingredientsAdapter = IngredientsAdapter(listOf(), requireContext()) {
            ingredient -> openMealsActivityByIngredient(ingredient)
        }

        rcMealIngredients.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = ingredientsAdapter
        }
    }

    private fun openMealsActivityByIngredient(ingredient: String?) {
        val actionMealDatailsFragmentToFilteredMealsByIngredientFragment =
            MealDatailsFragmentDirections.actionMealDatailsFragmentToFilteredMealsByIngredientFragment(ingredient)
        findNavController().navigate(actionMealDatailsFragmentToFilteredMealsByIngredientFragment)
    }

    private fun observeMealDetails() {
        addMealToPlansViewModel.message.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_SHORT).show()
        }

        addToFavoriteViewModel.mealDetails.observe(viewLifecycleOwner) { meal ->
            meal?.let {
                it.strMealThumb?.let { loadMealImage(it) }
                mealTitle.text = it.strMeal
                mealCategory.text = formatCategoryAppearence(it)
                mealInstructions.text = it.strInstructions
                loadYouTubeVideo(it)
                showIngredients(it)
                setupMealSourceLink(it)
                setUpListeners(it)
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

    private fun setUpListeners(filteredMeals: MealModel) {
        btnAddToPlan.setOnClickListener {
            dateTimePickerHelper.showFullDateTimePicker(
                onDateTimeSelected = { dateTimeInMillis ->
                    val mealPlan = createMealPlan(filteredMeals, dateTimeInMillis)
                    if (mealPlan?.let { it1 -> MealValidator.isValid(it1) } == true) {
                        addMealToPlansViewModel.addPlan(mealPlan)
                        scheduleMealNotification(filteredMeals.strMeal, dateTimeInMillis)
                        showSuccessMessage()
                    } else {
                        showErrorMessage()
                    }
                },
                onTimePickerShown = {Log.d("DateTimePicker", "Time picker shown")},
                onDatePickerShown = {Log.d("DateTimePicker", "Date picker shown")}
            )
        }
        mealCalImgView.setOnClickListener {
            dateTimePickerHelper.showFullDateTimePicker(
                onDateTimeSelected = { dateTimeInMillis ->
                    filteredMeals.strMeal?.let { mealName ->
                        addToDeviceCalendar(mealName, dateTimeInMillis)
                    }
                },
                onTimePickerShown = {Log.d("DateTimePicker", "Time picker shown")},
                onDatePickerShown = {Log.d("DateTimePicker", "Date picker shown")}
            )
        }

        mealFavImgView.setOnClickListener {
            toggleFavoriteStatus(filteredMeals)
        }
        filteredMeals.idMeal?.let { updateFavoriteIcon(it) }
    }

    private fun toggleFavoriteStatus(meal: MealModel) {
        val filteredMeal = meal.idMeal?.let {
            FilteredMealModel(
                strMeal = meal.strMeal,
                strMealThumb = meal.strMealThumb,
                idMeal = it
            )
        }

        lifecycleScope.launch {
            if (meal.idMeal?.let { isMealFavorite(it) } == true) {
                addToFavoriteViewModel.removeFilteredMeals(filteredMeal)
                mealFavImgView.setImageResource(R.drawable.ic_favorite_border)
                Snackbar.make(requireView(), "Removed from favorites", Snackbar.LENGTH_SHORT).show()
            } else {
                addToFavoriteViewModel.saveFilteredMeals(filteredMeal)
                mealFavImgView.setImageResource(R.drawable.ic_favorite_filled)
                Snackbar.make(requireView(), "Added to favorites", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun isMealFavorite(mealId: String): Boolean {
        return addToFavoriteViewModel.filteredMealsList.value?.any { it?.idMeal == mealId } ?: false
    }

    private fun updateFavoriteIcon(mealId: String) {
        lifecycleScope.launch {
            val isFavorite = isMealFavorite(mealId)
            mealFavImgView.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )
        }
    }

    private fun addToDeviceCalendar(title: String?, startMillis: Long) {
        title?.let {
            val endMillis = startMillis + TimeUnit.HOURS.toMillis(1)

            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.DESCRIPTION, "Meal Reminder")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }

            try {
                startActivity(intent)
                Snackbar.make(requireView(), "Opening calendar to add event", Snackbar.LENGTH_SHORT).show()
            } catch (e: ActivityNotFoundException) {
                Snackbar.make(requireView(), "No calendar app found", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun createMealPlan(meal: MealModel, dateTime: Long): AddMealModel? {
        return meal.strMeal?.let { name ->
            meal.strMealThumb?.let { thumb ->
                meal.strCategory?.let { category ->
                    AddMealModel(
                        thumbMealPlan = thumb,
                        nameMealPlan = name,
                        categoryMealPlan = category,
                        dateMealPlan = dateTime,
                    )
                }
            }
        }
    }

    private fun scheduleMealNotification(mealName: String?, dateTime: Long) {
        mealName?.let {
            SchedulerMealNotification.scheduleMealNotification(
                requireContext(),
                dateTime,
                it
            )
        }
    }

    private fun showSuccessMessage() {
        Snackbar.make(
            requireView(),
            "Meal added to plan successfully",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showErrorMessage() {
        Snackbar.make(requireView(), "Error adding meal to plan", Snackbar.LENGTH_SHORT).show()
    }
}