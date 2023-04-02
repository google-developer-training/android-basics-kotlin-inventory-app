package com.example.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientListAdapter(private val usedIngredients: List<Ingredient>) :
    RecyclerView.Adapter<IngredientListAdapter.UsedIngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsedIngredientViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.used_ingredient_list_item, parent, false)
        return UsedIngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsedIngredientViewHolder, position: Int) {
        val usedIngredient = usedIngredients[position]
        holder.usedIngredientName.text = usedIngredient.name
    }

    override fun getItemCount(): Int {
        return usedIngredients.size
    }

        class UsedIngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val usedIngredientName: TextView = itemView.findViewById(R.id.ingredient_name)
        }
    }


