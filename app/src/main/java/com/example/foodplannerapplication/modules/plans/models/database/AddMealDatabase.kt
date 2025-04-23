package com.example.foodplannerapplication.modules.plans.models.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.foodplannerapplication.modules.plans.models.dao.AddMealDao
import com.example.foodplannerapplication.modules.plans.models.entity.AddMealModel

@Database(entities = [AddMealModel::class], version = 5)
abstract class AddMealDatabase : RoomDatabase() {
    abstract fun getAddMealDao(): AddMealDao

    companion object {
        @Volatile
        private var INSTANCE: AddMealDatabase? = null

        fun getDatabase(ctx: Context): AddMealDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    AddMealDatabase::class.java,
                    "weekly_plan_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}