package com.example.gourmetgalaxy

data class Recipe(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val duration: String = "",
    val ingredients: String = "",
    val instructions: String = "",
    var imageUri: String? = null,
    val mealCourse: String = ""
)
