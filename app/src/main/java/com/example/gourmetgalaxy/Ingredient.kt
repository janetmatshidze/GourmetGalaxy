package com.example.gourmetgalaxy

import android.content.Context
import org.json.JSONObject

data class Ingredient(val id: Int, val name: String,var isPurchased: Boolean = false)

fun loadIngredients(context: Context): List<Ingredient> {
    val ingredientList = mutableListOf<Ingredient>()
    val jsonString = context.assets.open("ingredients.json").bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(jsonString)
    val jsonArray = jsonObject.getJSONArray("ingredients")

    for (i in 0 until jsonArray.length()) {
        val ingredientObject = jsonArray.getJSONObject(i)
        val ingredient = Ingredient(
            id = ingredientObject.getInt("id"),
            name = ingredientObject.getString("name")
        )
        ingredientList.add(ingredient)
    }

    return ingredientList
}
