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
        fetchRecipes() // Fetch recipes with real-time updates
    }

     fun fetchRecipes() {
        // Listen for real-time updates in Firestore
        recipesListener = firestore.collection("recipes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("RecipeViewModel", "Error fetching recipes: ", error)
                    return@addSnapshotListener
                }

                val recipeList = mutableListOf<Recipe>()
                snapshot?.forEach { document ->
                    val recipe = document.toObject(Recipe::class.java)
                    recipeList.add(recipe)
                }
                _recipes.value = recipeList
                updateFavoriteRecipes() // Update favorites whenever recipes change
            }
    }

    fun toggleFavorite(recipe: Recipe) {
        recipe.isFavorite = !recipe.isFavorite
        updateFavoriteInFirestore(recipe)
        updateFavoriteRecipes() // Refresh favorite recipes list
    }

    private fun updateFavoriteRecipes() {
        val favorites = _recipes.value?.filter { it.isFavorite } ?: emptyList()
        _favoriteRecipes.value = favorites // Update favorite recipes LiveData
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

    override fun onCleared() {
        super.onCleared()
        // Detach the listener when ViewModel is cleared to prevent memory leaks
        recipesListener?.remove()
    }
}
