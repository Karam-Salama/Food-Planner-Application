package com.example.foodplannerapplication.modules.home.data.model

data class CategoryResponse(
    val categories: List<CategoryModel?>?
)

data class CategoryModel(
    val idCategory: String?,
    val strCategory: String?,
    val strCategoryDescription: String?,
    val strCategoryThumb: String?
)