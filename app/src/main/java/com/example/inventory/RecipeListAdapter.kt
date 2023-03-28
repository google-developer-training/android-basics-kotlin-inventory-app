package com.example.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecipeListAdapter(private val recipesList: Array<Recipe>) : RecyclerView.Adapter<RecipeListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recipe_list_item,
        parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return recipesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val recipe = recipesList[position]
        Picasso.get().load(recipe.image).into(holder.recipeImage)
        holder.recipeName.text = recipe.title
        val bundle = bundleOf("recipeTitle" to recipe.title, "recipeImage" to recipe.image)
        holder.itemView.setOnClickListener {
            it.findNavController().navigate(R.id.recipeDetailFragment, bundle)
        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val recipeImage: ImageView = itemView.findViewById(R.id.recipe_image)
        val recipeName : TextView = itemView.findViewById(R.id.recipe_name)


    }

}

