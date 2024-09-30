package com.example.gourmetgalaxy

import RecipeViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: RecipeViewModel
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()

        // Set up Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Use requireContext() to get the appropriate context
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)

        // Set up RecyclerView with LinearLayoutManager (single column)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recipesRecyclerView)
        val layoutManager = LinearLayoutManager(requireContext()) // Single column
        recyclerView.layoutManager = layoutManager

        // Fetch recipes when the fragment is created
        viewModel.fetchRecipes()

        // Observe the recipes and update the adapter
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            // Pass a lambda to handle favorite button clicks
            recipeAdapter = RecipeAdapter(recipes) { recipe ->
                handleFavoriteClick(recipe) // Call the function to handle favorites
            }
            recyclerView.adapter = recipeAdapter
        }

        // Display the user's name
        val textView = view.findViewById<TextView>(R.id.name)
        val user = mAuth.currentUser

        if (user != null) {
            val userName = user.displayName
            textView.text = "Welcome, $userName"
        } else {
            // Handle the case where the user is not signed in
            textView.text = "Welcome, Guest"
        }

        return view
    }

    private fun handleFavoriteClick(recipe: Recipe) {
        // Add your logic to handle favorite clicks here
        // For example, toggle favorite status and update the UI
        // You can also notify the ViewModel to update the favorite state in the database
    }
}
