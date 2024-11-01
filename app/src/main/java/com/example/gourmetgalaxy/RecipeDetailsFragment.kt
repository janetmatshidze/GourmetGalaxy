package com.example.gourmetgalaxy

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

class RecipeDetailsFragment : Fragment() {

    private lateinit var ratingBar: RatingBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recipe_details, container, false)


        // Retrieve data from the Bundle
        val recipeImageUri = arguments?.getString("recipeImageUri")
        val recipeTitle = arguments?.getString("recipeTitle")
        val recipeDescription = arguments?.getString("recipeDescription")
        val recipeDuration = arguments?.getString("recipeDuration")
        val recipeIngredients = arguments?.getString("recipeIngredients")
        val recipeInstructions = arguments?.getString("recipeInstructions")
        val recipeRating = arguments?.getFloat("recipeRating", 0f) ?: 0f // Get rating
        val backButton=view.findViewById<ImageButton>(R.id.backButton)

        // Load the image using Glide
        Glide.with(this)
            .load(Uri.parse(recipeImageUri))
            .into(view.findViewById(R.id.recipeImage))

        // Bind the data to the views
        view.findViewById<TextView>(R.id.recipeTitle).text = recipeTitle
        view.findViewById<TextView>(R.id.recipeDescription).text = recipeDescription
        view.findViewById<TextView>(R.id.recipeDuration).text = recipeDuration
        view.findViewById<TextView>(R.id.ingredientsList).text = recipeIngredients
        view.findViewById<TextView>(R.id.instructionsList).text = recipeInstructions

        // Initialize and set the rating bar
        ratingBar = view.findViewById(R.id.ratingBar)
        ratingBar.rating = recipeRating // Set initial rating from the arguments


        backButton.setOnClickListener {

            findNavController().navigate(R.id.action_recipeDetailsFragment_to_homeFragment)
        }


        return view
    }
}
