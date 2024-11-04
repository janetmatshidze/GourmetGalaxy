package com.example.gourmetgalaxy

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientAdapter(

    private var ingredientList: MutableList<Ingredient>,
    private val onIngredientClicked: (Ingredient) -> Unit,
    private val onPurchasedClicked: (Ingredient) -> Unit,
    private val onListChanged: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    private var shoppingList = mutableListOf<Ingredient>()
    private var purchasedList = mutableListOf<Ingredient>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ingredient, parent, false)
            IngredientViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < 0 || position >= itemCount) return // Safety check for position bounds

        if (holder is HeaderViewHolder) {
            if (position == 0 && shoppingList.isNotEmpty()) {
                holder.bind("Your Shopping List")
            } else if (position == shoppingList.size + 1 && purchasedList.isNotEmpty()) {
                holder.bind("Purchased Items")
            }
        } else if (holder is IngredientViewHolder) {
            val itemPosition = if (position <= shoppingList.size) position - 1 else position - 2 - shoppingList.size
            val ingredient = if (position <= shoppingList.size) shoppingList[itemPosition] else purchasedList[itemPosition]
            holder.bind(ingredient)
        }
    }

    override fun getItemCount(): Int {
        val headerCount = (if (shoppingList.isNotEmpty()) 1 else 0) + (if (purchasedList.isNotEmpty()) 1 else 0)
        return shoppingList.size + purchasedList.size + headerCount
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == 0 && shoppingList.isNotEmpty() -> VIEW_TYPE_HEADER
            position == shoppingList.size + 1 && purchasedList.isNotEmpty() -> VIEW_TYPE_HEADER
            else -> VIEW_TYPE_ITEM
        }
    }

    fun updateShoppingList(newList: List<Ingredient>) {
        shoppingList.clear()
        shoppingList.addAll(newList)
        notifyDataSetChanged()
        onListChanged()
    }

    fun updatePurchasedList(newList: List<Ingredient>) {
        purchasedList.clear()
        purchasedList.addAll(newList)
        notifyDataSetChanged()
        onListChanged()
    }

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.ingredientName)
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(ingredient: Ingredient) {
            nameTextView.text = ingredient.name
            checkBox.isChecked = purchasedList.contains(ingredient)
            updateTextAppearance(checkBox.isChecked)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    shoppingList.remove(ingredient)
                    purchasedList.add(ingredient)
                } else {
                    purchasedList.remove(ingredient)
                    shoppingList.add(ingredient)
                }
                updateTextAppearance(isChecked)
                notifyDataSetChanged()
                onListChanged()
            }
            deleteButton.setOnClickListener {
                shoppingList.remove(ingredient)
                purchasedList.remove(ingredient)
                notifyDataSetChanged()
                onListChanged()
            }
        }

        private fun updateTextAppearance(purchased: Boolean) {
            nameTextView.paintFlags = if (purchased) {
                nameTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                nameTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTitle: TextView = itemView.findViewById(R.id.headerText)
        fun bind(title: String) {
            headerTitle.text = title
        }
    }
}
