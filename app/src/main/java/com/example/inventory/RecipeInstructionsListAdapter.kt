package com.example.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeInstructionsListAdapter(private val recipesInstructionsList: MutableList<RecipeInstructions>) : RecyclerView.Adapter<RecipeInstructionsListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recipe_instructions_list_item,
            parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return recipesInstructionsList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val recipeInstruction = recipesInstructionsList[position]
        holder.recipeStep.text = recipeInstruction.number.toString() + "."
        holder.recipeInstruction.text = recipeInstruction.step
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val recipeStep: TextView = itemView.findViewById(R.id.recipe_step_number)
        val recipeInstruction: TextView = itemView.findViewById(R.id.recipe_instruction)

    }

}