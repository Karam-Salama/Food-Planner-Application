package com.example.foodplannerapplication.modules.favorite.models
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodplannerapplication.core.data.models.FilteredMealModel

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM Favorites_table")
    suspend fun getAllFavorites(): List<FilteredMealModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFilteredMealsToFavorites(filteredMeal: FilteredMealModel): Long

    @Delete
    suspend fun removeFilteredMealsFromFavorites(filteredMeal: FilteredMealModel): Int
}