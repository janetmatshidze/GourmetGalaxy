package com.example.gourmetgalaxy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddIngredientsFragment : Fragment() {

    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var emptyStateText: TextView
    private val allIngredients by lazy { loadIngredients(requireContext()) }
    private var filteredList = mutableListOf<Ingredient>()
    private val shoppingList = mutableListOf<Ingredient>()
    private val purchasedList = mutableListOf<Ingredient>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        return inflater.inflate(R.layout.fragment_add_ingredients, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyStateText = view.findViewById(R.id.emptyStateText)

        ingredientAdapter = IngredientAdapter(
            ingredientList = filteredList,
            onIngredientClicked = { ingredient -> onIngredientClicked(ingredient) },
            onPurchasedClicked = { ingredient -> onPurchasedClicked(ingredient) },
            onListChanged = ::toggleEmptyState
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ingredientAdapter

        loadShoppingList()
        loadPurchasedList()

        ingredientAdapter.updateShoppingList(shoppingList)
        ingredientAdapter.updatePurchasedList(purchasedList)

        val searchView = view.findViewById<TextInputEditText>(R.id.searchView)
        searchView.addTextChangedListener {
            val query = it.toString()
            filteredList = if (query.isNotEmpty()) {
                allIngredients.filter { ingredient ->
                    ingredient.name.contains(query, ignoreCase = true)
                }.toMutableList()
            } else {
                allIngredients.toMutableList()
            }
            ingredientAdapter.updateShoppingList(filteredList)
            toggleEmptyState()
        }

        toggleEmptyState()
    }

    private fun onIngredientClicked(ingredient: Ingredient) {
        if (!shoppingList.contains(ingredient) && !purchasedList.contains(ingredient)) {
            shoppingList.add(ingredient)
            saveShoppingList()
            ingredientAdapter.updateShoppingList(shoppingList)
            toggleEmptyState()
        }
    }

    private fun onPurchasedClicked(ingredient: Ingredient) {
        if (shoppingList.contains(ingredient)) {
            shoppingList.remove(ingredient)
            purchasedList.add(ingredient)
            saveShoppingList()
            savePurchasedList()
            toggleEmptyState()
        }
        ingredientAdapter.updatePurchasedList(purchasedList)
    }

    private fun toggleEmptyState() {
        emptyStateText.text = when {
            shoppingList.isEmpty() && purchasedList.isEmpty() -> "Your shopping list is empty"
            else -> ""
        }
        emptyStateText.visibility = if (shoppingList.isEmpty() && purchasedList.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun saveShoppingList() {
        val prefs = requireContext().getSharedPreferences("GourmetGalaxyPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(shoppingList)
        editor.putString("shoppingList", json)
        editor.apply()
    }

    private fun savePurchasedList() {
        val prefs = requireContext().getSharedPreferences("GourmetGalaxyPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(purchasedList)
        editor.putString("purchasedList", json)
        editor.apply()
    }

    private fun loadShoppingList() {
        val prefs = requireContext().getSharedPreferences("GourmetGalaxyPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("shoppingList", null)
        val type = object : TypeToken<MutableList<Ingredient>>() {}.type
        val savedList: MutableList<Ingredient>? = gson.fromJson(json, type)
        savedList?.let {
            shoppingList.clear()
            shoppingList.addAll(it)
        }
    }

    private fun loadPurchasedList() {
        val prefs = requireContext().getSharedPreferences("GourmetGalaxyPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("purchasedList", null)
        val type = object : TypeToken<MutableList<Ingredient>>() {}.type
        val savedList: MutableList<Ingredient>? = gson.fromJson(json, type)
        savedList?.let {
            purchasedList.clear()
            purchasedList.addAll(it)
        }
    }
}