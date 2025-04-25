package com.example.foodplannerapplication.core.helpers
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.io.File

class MealImageHandler(private val context: Context) {
    fun getImagePath(imageView: ImageView): String {
        val bitmap = (imageView.drawable as? BitmapDrawable)?.bitmap ?: return ""
        val file = File(context.filesDir, "meal_${System.currentTimeMillis()}.jpg")

        file.outputStream().use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        }

        return file.absolutePath
    }
}
