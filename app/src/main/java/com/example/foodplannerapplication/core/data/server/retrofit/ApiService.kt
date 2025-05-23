package com.example.foodplannerapplication.core.data.server.retrofit


import com.example.foodplannerapplication.modules.home.data.model.AreaResponse
import com.example.foodplannerapplication.modules.home.data.model.CategoryResponse
import com.example.foodplannerapplication.core.data.models.FilterdMealsResponse
import com.example.foodplannerapplication.modules.home.data.model.IngredientsResponse
import com.example.foodplannerapplication.modules.home.data.model.RandomMealResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
    // https://www.themealdb.com/api/json/v1/1/random.php
    @GET("random.php")
    suspend fun getMealOfTheDay(): RandomMealResponse

    // https://www.themealdb.com/api/json/v1/1/lookup.php?i=52848
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String?): RandomMealResponse

    // https://www.themealdb.com/api/json/v1/1/categories.php
    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    // https://www.themealdb.com/api/json/v1/1/list.php?a=list
    @GET("list.php?a=list")
    suspend fun getAreas(): AreaResponse

    // https://www.themealdb.com/api/json/v1/1/list.php?i=list
    @GET("list.php?i=list")
    suspend fun getIngredients(): IngredientsResponse

    // https://www.themealdb.com/api/json/v1/1/filter.php?c=Beef
    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String?): FilterdMealsResponse

    // https://www.themealdb.com/api/json/v1/1/filter.php?a=Italian
    @GET("filter.php")
    suspend fun getMealsByArea(@Query("a") area: String?): FilterdMealsResponse

    // https://www.themealdb.com/api/json/v1/1/filter.php?i=chicken
    @GET("filter.php")
    suspend fun getMealsByIngredient(@Query("i") ingredient: String?): FilterdMealsResponse

    // https://www.themealdb.com/images/icons/flags/big/64/us.png
    @GET("images/icons/flags/big/64/{flag}.png")
    suspend fun getFlag(@Path("flag") flag: String)
}

object RetrofitHelper {
    private val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService = retrofit.create(APIService::class.java)
}