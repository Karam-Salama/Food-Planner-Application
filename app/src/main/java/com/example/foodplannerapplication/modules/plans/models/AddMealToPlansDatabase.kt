package com.example.foodplannerapplication.modules.plans.models
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AddMealModel::class], version = 5)
abstract class AddMealToPlansDatabase : RoomDatabase() {
    abstract fun getAddMealToPlansDao(): AddMealToPlansDao

    companion object {
        @Volatile
        private var INSTANCE: AddMealToPlansDatabase? = null

        fun getDatabase(ctx: Context): AddMealToPlansDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    AddMealToPlansDatabase::class.java,
                    "weekly_plan_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}