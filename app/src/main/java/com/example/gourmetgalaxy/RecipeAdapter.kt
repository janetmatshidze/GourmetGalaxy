package com.example.gourmetgalaxy

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class RecipeAdapter(
    private var recipes: List<Recipe>,
    private val onFavoriteClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    fun updateRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged() // Notify that data has changed to refresh the list
    }
    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeImage: ImageView = view.findViewById(R.id.recipeImage)
        val recipeTitle: TextView = view.findViewById(R.id.recipeTitle)
        val recipeDescription: TextView = view.findViewById(R.id.recipeDescription)
        val recipeDuration: TextView = view.findViewById(R.id.recipeDuration)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val favoriteButton: ImageButton = view.findViewById(R.id.favoriteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        // Bind the recipe data to the views
        holder.recipeTitle.text = recipe.title
        holder.recipeDescription.text = recipe.description
        holder.recipeDuration.text = recipe.duration
        holder.ratingBar.rating = recipe.rating

        // Load the image using Glide
        Glide.with(holder.itemView.context)
            .load(recipe.imageUri?.let { Uri.parse(it) })
            .into(holder.recipeImage)

        // Set the favorite button icon based on the recipe's favorite status
        holder.favoriteButton.setImageResource(
            if (recipe.isFavorite) R.drawable.ic_bookmark_border2
            else R.drawable.ic_bookmark_border
        )

        // Handle item click to navigate to recipe details
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("recipeImageUri", recipe.imageUri)
                putString("recipeTitle", recipe.title)
                putString("recipeDescription", recipe.description)
                putString("recipeDuration", recipe.duration)
                putString("recipeIngredients", recipe.ingredients)
                putString("recipeInstructions", recipe.instructions)
                putFloat("recipeRating", recipe.rating)
            }

            holder.itemView.findNavController().navigate(
                R.id.action_recipeHomeFragment_to_recipeDetailsFragment, bundle
            )
        }

        // Handle favorite button click, delegating the action to the ViewModel
        holder.favoriteButton.setOnClickListener {
            onFavoriteClick(recipe) // Toggle favorite status in ViewModel

            // Update the button icon based on the new favorite status
            holder.favoriteButton.setImageResource(
                if (recipe.isFavorite) R.drawable.ic_bookmark_border2
                else R.drawable.ic_bookmark_border
            )
        }

        // Handle rating bar change
        holder.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            recipe.rating = rating // Update the recipe rating
            saveRecipeRatingToFirestore(recipe) // Save updated rating to Firestore
        }
    }

    override fun getItemCount(): Int = recipes.size
}

// Function to save the updated rating to Firestore
private fun saveRecipeRatingToFirestore(recipe: Recipe) {
    FirebaseFirestore.getInstance().collection("recipes").document(recipe.id)
        .update("rating", recipe.rating)
        .addOnSuccessListener {
            Log.d("RecipeAdapter", "Rating updated successfully")
        }
        .addOnFailureListener { e ->
            Log.e("RecipeAdapter", "Error updating rating: ", e)
        }
}
