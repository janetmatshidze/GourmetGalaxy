package com.example.gourmetgalaxy

import android.content.Context
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
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)

        // Set up RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.recipesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize the adapter once and set it to the RecyclerView
        recipeAdapter = RecipeAdapter(emptyList()) { recipe ->
            viewModel.toggleFavorite(recipe)
        }
        recyclerView.adapter = recipeAdapter

        // Observe the recipes list and update the adapter
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipes?.let {
                recipeAdapter.updateRecipes(it)
            }
        }

        // Display the user's name in the TextView
        val textView = view.findViewById<TextView>(R.id.name)
        val user = mAuth.currentUser
        textView.text = "Welcome, ${user?.displayName ?: "Guest"}"

        return view // Moved to the end of the method
    }

    private fun onRecipeRated(recipe: Recipe) {
        // This method will be called when a recipe is rated
        saveNotification("Recipe rated: ${recipe.title}") // Save notification when a recipe is rated
    }

    private fun saveNotification(message: String) {
        val preferences = requireActivity().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val notifications = preferences.getStringSet("notifications", mutableSetOf()) ?: mutableSetOf()
        notifications.add(message)
        preferences.edit().putStringSet("notifications", notifications).apply()
    }
}
