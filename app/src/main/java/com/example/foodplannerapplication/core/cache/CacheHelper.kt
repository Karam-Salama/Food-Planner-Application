package com.example.foodplannerapplication.core.cache

import android.content.Context
import android.content.SharedPreferences

object CacheHelper {
    private lateinit var sharedPreferences: SharedPreferences

    // تهيئة SharedPreferences
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    }

    // حفظ بيانات في SharedPreferences
    fun saveData(key: String, value: Any) {
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
        }
        editor.apply()
    }

    // جلب بيانات من SharedPreferences
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    // إزالة بيانات باستخدام مفتاح محدد
    fun removeData(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    // حذف جميع البيانات
    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }
}

