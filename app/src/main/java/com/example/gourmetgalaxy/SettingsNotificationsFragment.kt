package com.example.gourmetgalaxy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.messaging.FirebaseMessaging

class SettingsNotificationsFragment : Fragment() {

    private lateinit var switchNewRecipes: SwitchMaterial
    private lateinit var switchRecipeRatings: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings_notifications, container, false)

        val toolBarBtn: View = view.findViewById(R.id.topAppBar)
        toolBarBtn.setOnClickListener {
            findNavController().navigate(R.id.action_settingsnotificationsFragment_to_settingsFragment)
        }
        // Initialize switches
        switchNewRecipes = view.findViewById(R.id.switch_new_recipes)
        switchRecipeRatings = view.findViewById(R.id.switch_recipe_ratings)

        // Load saved preferences
        val preferences = requireActivity().getSharedPreferences("NotificationPrefs", 0)
        switchNewRecipes.isChecked = preferences.getBoolean("newRecipesEnabled", false)
        switchRecipeRatings.isChecked = preferences.getBoolean("recipeRatingsEnabled", false)

        // Set listeners for the switches
        switchNewRecipes.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean("newRecipesEnabled", isChecked).apply()
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("new_recipes")
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("new_recipes")
            }
        }

        switchRecipeRatings.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean("recipeRatingsEnabled", isChecked).apply()
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("recipe_ratings")
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("recipe_ratings")
            }
        }


        return view
    }
}
