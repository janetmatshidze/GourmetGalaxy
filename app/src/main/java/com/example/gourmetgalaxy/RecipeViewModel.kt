package com.example.gourmetgalaxy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class RecipeViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val _favoriteRecipes = MutableLiveData<List<Recipe>>()
    val favoriteRecipes: LiveData<List<Recipe>> get() = _favoriteRecipes

    init {
        fetchRecipes() // Fetch recipes when ViewModel is initialized
    }

    fun fetchRecipes() {
        firestore.collection("recipes")
            .get()
            .addOnSuccessListener { documents ->
                val recipeList = mutableListOf<Recipe>()
                for (document in documents) {
                    val recipe = document.toObject(Recipe::class.java)
                    recipeList.add(recipe)
                }
                _recipes.value = recipeList // Update LiveData with the fetched recipes
                updateFavoriteRecipes() // Update favorite recipes whenever the list changes
            }
    }

    fun toggleFavorite(recipe: Recipe) {
        recipe.isFavorite = !recipe.isFavorite
        // Update the Firestore document to reflect the change
        updateRecipeInFirestore(recipe)
        updateFavoriteRecipes() // Update the favorite recipes list after toggling
    }

    private fun updateFavoriteRecipes() {
        val favorites = _recipes.value?.filter { it.isFavorite } ?: emptyList()
        _favoriteRecipes.value = favorites // Update favorite recipes LiveData
    }

    private fun updateRecipeInFirestore(recipe: Recipe) {
        firestore.collection("recipes")
            .document(recipe.id) // Assuming Recipe has an 'id' field
            .set(recipe)
            .addOnSuccessListener {
                // Handle successful update
            }
            .addOnFailureListener {
                // Handle failure
            }
    }
}
