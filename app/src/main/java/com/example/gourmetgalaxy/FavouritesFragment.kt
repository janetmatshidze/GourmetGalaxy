package com.example.gourmetgalaxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavouritesFragment : Fragment() {
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var favouritesAdapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        recipeViewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)

        val imageView: ImageView = view.findViewById(R.id.imageView3)
        val textView: TextView = view.findViewById(R.id.textView9)
        val favouritesRecyclerView: RecyclerView = view.findViewById(R.id.favouritesRecyclerView)
        val favouriteRecipeBtn: AppCompatButton = view.findViewById(R.id.AddFavouriteRecipeBtn)

        favouritesRecyclerView.layoutManager = LinearLayoutManager(context)

        recipeViewModel.favoriteRecipes.observe(viewLifecycleOwner) { recipes ->
            if (recipes.isEmpty()) {
                favouritesRecyclerView.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                favouriteRecipeBtn.visibility = View.VISIBLE
            } else {
                favouritesRecyclerView.visibility = View.VISIBLE
                imageView.visibility = View.GONE
                textView.visibility = View.GONE
                favouriteRecipeBtn.visibility = View.GONE

                favouritesAdapter = RecipeAdapter(recipes) { recipe ->
                    recipeViewModel.toggleFavorite(recipe)
                }
                favouritesRecyclerView.adapter = favouritesAdapter
                favouritesAdapter.notifyDataSetChanged()
            }
        }

        favouriteRecipeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_favouritesFragment_to_homeFragment)
        }

        return view
    }
}
