package com.example.gourmetgalaxy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class ShoppingListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shoppinglist, container, false)

        val addIngredientBtn: View = view.findViewById(R.id.AddIngredientsBtn)

        addIngredientBtn.setOnClickListener {

            findNavController().navigate(R.id.action_shoppinglist_to_addingredientsFragment)
        }

        return view
    }
}