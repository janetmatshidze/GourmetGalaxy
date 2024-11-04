package com.example.gourmetgalaxy

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class RecipeViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val _favoriteRecipes = MutableLiveData<List<Recipe>>()
    val favoriteRecipes: LiveData<List<Recipe>> get() = _favoriteRecipes

    private var recipesListener: ListenerRegistration? = null

    init {
        fetchRecipes() // Start real-time updates
    }

     fun fetchRecipes() {
        recipesListener = firestore.collection("recipes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("RecipeViewModel", "Error fetching recipes: ", error)
                    return@addSnapshotListener
                }

                val recipeList = snapshot?.mapNotNull { document ->
                    document.toObject(Recipe::class.java).apply {
                        id = document.id // Ensure ID is set
                    }
                } ?: emptyList()

                _recipes.value = recipeList
                updateFavoriteRecipes()
            }
    }

    fun toggleFavorite(recipe: Recipe) {
        recipe.isFavorite = !recipe.isFavorite
        updateFavoriteInFirestore(recipe)
        updateFavoriteRecipes()
    }

    private fun updateFavoriteRecipes() {
        _favoriteRecipes.value = _recipes.value?.filter { it.isFavorite }
    }

    private fun updateFavoriteInFirestore(recipe: Recipe) {
        firestore.collection("recipes")
            .document(recipe.id)
            .update("isFavorite", recipe.isFavorite)
            .addOnSuccessListener {
                Log.d("RecipeViewModel", "Favorite status updated for recipe: ${recipe.id}")
            }
            .addOnFailureListener { e ->
                Log.e("RecipeViewModel", "Error updating favorite status: ", e)
            }
    }

    fun updateRecipeRating(recipe: Recipe, newRating: Float) {
        recipe.rating = newRating
        firestore.collection("recipes")
            .document(recipe.id)
            .update("rating", newRating)
            .addOnSuccessListener {
                Log.d("RecipeViewModel", "Rating updated for recipe: ${recipe.id}")
            }
            .addOnFailureListener { e ->
                Log.e("RecipeViewModel", "Error updating rating: ", e)
            }
    }

    override fun onCleared() {
        super.onCleared()
        recipesListener?.remove()
    }
}
