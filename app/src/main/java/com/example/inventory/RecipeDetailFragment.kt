package com.example.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

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
        val recipe = args.recipe

        val recipeNameText = view.findViewById<TextView>(R.id.recipe_name)
        recipeNameText.text = recipe.title
        val recipeImageView = view.findViewById<ImageView>(R.id.recipe_image)
        Picasso.get().load(recipe.image).into(recipeImageView)

        val spacing = resources.getDimensionPixelSize(R.dimen.item_spacing)

        val usedIngredientsRecyclerView = view.findViewById<RecyclerView>(R.id.used_ingredient_recycler_view)
        val usedIngredientLayoutManager = GridLayoutManager(this.activity, 2)
        usedIngredientsRecyclerView.layoutManager = usedIngredientLayoutManager

        val usedIngredientAdapter = IngredientListAdapter(recipe.usedIngredients)
        usedIngredientsRecyclerView.adapter = usedIngredientAdapter
        usedIngredientsRecyclerView.addItemDecoration(ItemSpacingDecoration(spacing))

        val missedIngredientsRecyclerView = view.findViewById<RecyclerView>(R.id.missed_ingredient_recycler_view)
        val missedIngredientLayoutManager = GridLayoutManager(this.activity, 2)
        missedIngredientsRecyclerView.layoutManager = missedIngredientLayoutManager

        val missedIngredientAdapter = IngredientListAdapter(recipe.missedIngredients)
        missedIngredientsRecyclerView.adapter = missedIngredientAdapter
        missedIngredientsRecyclerView.addItemDecoration(ItemSpacingDecoration(spacing))

        getRecipeInformation(recipe.id)

    }

    private fun getRecipeInformation(recipeId: Int) {
        val url = "https://api.spoonacular.com/recipes/" + recipeId.toString() + "/information?apiKey=33dde6e60fde4a11bc1040e239a4fdb3"
        val apiService = MyApiService()
        apiService.makeApiRequest(url, MyCallback(this))
    }

    class MyApiService {
        fun makeApiRequest(url: String, callback: Callback) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(callback)
        }
    }

    class MyCallback(private val fragment: RecipeDetailFragment) : Callback {
        override fun onResponse(call: okhttp3.Call, response: Response) {
            val responseBody = response.body?.string()
            val recipeInformation = Gson().fromJson(responseBody, RecipeInformation::class.java)
            fragment.activity?.runOnUiThread {
                val recipeCookingTime = fragment.view?.findViewById<TextView>(R.id.recipe_cooking_time)
                if (recipeCookingTime != null) {
                    val formatRecipeCookingTime = "Cooking Time: " + recipeInformation.readyInMinutes + " Minutes"
                    recipeCookingTime.text = formatRecipeCookingTime
                }
                val dietaryRestrictionRecyclerView = fragment.view?.findViewById<RecyclerView>(R.id.dietary_restrictions_recycler_view)
                val dietaryRestrictionLayoutManager = LinearLayoutManager(fragment.activity, LinearLayoutManager.HORIZONTAL, false)
                if (dietaryRestrictionRecyclerView != null) {
                    dietaryRestrictionRecyclerView.layoutManager = dietaryRestrictionLayoutManager
                    val lstStrings = mutableListOf<String>()
                    if(recipeInformation.dairyFree) lstStrings.add("Dairy Free")
                    if(recipeInformation.glutenFree) lstStrings.add("Gluten Free")
                    if(recipeInformation.vegan) lstStrings.add("Vegan")
                    if(recipeInformation.vegetarian) lstStrings.add("Vegetarian")
                    val dietaryRestrictionAdapter = DietaryRestrictionListAdapter(lstStrings)
                    dietaryRestrictionRecyclerView.adapter = dietaryRestrictionAdapter
                }
            }
        }

        override fun onFailure(call: okhttp3.Call, e: IOException) {
            // Handle failure
        }
    }
}