package com.example.gourmetgalaxy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView


class FavouritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_favourites, container, false)

        val favouriteRecipeBtn: AppCompatButton = view.findViewById(R.id.AddFavouriteRecipeBtn)
        val imageView: ImageView = view.findViewById(R.id.imageView3)
        val textView: TextView = view.findViewById(R.id.textView9)
        val favouritesRecyclerView: RecyclerView = view.findViewById(R.id.favouritesRecyclerView)
        favouriteRecipeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_favouritesFragment_to_homeFragment)
        }
        return view
    }

}
