package com.example.foodplannerapplication.modules.favorite.models
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foodplannerapplication.core.data.models.FilteredMealModel

@Database(entities = [FilteredMealModel::class], version = 2)
abstract class FavoritesDatabase : RoomDatabase() {
    abstract fun getFavoritesDao(): FavoritesDao

    companion object {
        @Volatile
        private var INSTANCE: FavoritesDatabase? = null

        fun getDatabase(ctx: Context): FavoritesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    FavoritesDatabase::class.java,
                    "favorites_db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
                INSTANCE = instance
                instance
            }
        }
    }
}