package com.example.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.squareup.picasso.Picasso
class RecipeDetailFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: RecipeDetailFragmentArgs by navArgs()
        val recipeName = args.recipeTitle
        val recipeImage = args.recipeImage
        val missedIngredients = args.missedIngredients
        println(missedIngredients)

        val recipeNameText = view.findViewById<TextView>(R.id.recipe_name)
        recipeNameText.text = recipeName
        val recipeImageView = view.findViewById<ImageView>(R.id.recipe_image)
        Picasso.get().load(recipeImage).into(recipeImageView)

    }
}