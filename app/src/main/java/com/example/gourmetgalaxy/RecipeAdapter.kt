package com.example.gourmetgalaxy

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Assuming you're using Glide for image loading

class RecipeAdapter(
    private val recipes: List<Recipe>,
    private val onFavoriteClick: (Recipe) -> Unit // Callback for favorite button clicks
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

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
        holder.ratingBar.rating = 0f // Set the rating; adjust based on your data

        // Load the image using Glide (or any other image loading library)
        Glide.with(holder.itemView.context)
            .load(recipe.imageUri?.let { Uri.parse(it) }) // Check if imageUri is not null
            .into(holder.recipeImage)

        // Handle favorite button clicks
        holder.favoriteButton.setOnClickListener {
            onFavoriteClick(recipe) // Call the passed callback function
        }
    }

    override fun getItemCount(): Int = recipes.size
}
