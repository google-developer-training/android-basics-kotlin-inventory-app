package com.example.inventory

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class RecipeInstructionsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_instructions, container, false)
    }

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val args: RecipeInstructionsFragmentArgs by navArgs()
    val recipeId = args.recipeId

    getRecipeInstructions(recipeId)
}
private fun getRecipeInstructions(recipeId: Int) {
    val url = "https://api.spoonacular.com/recipes/" + recipeId.toString() +  "/analyzedInstructions?apiKey=c08a9abc204a46908523eeddcf170c27"
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

class MyCallback(private val fragment: RecipeInstructionsFragment) : Callback {
    override fun onResponse(call: okhttp3.Call, response: Response) {
        if (response.isSuccessful) {
            val responseBody = response.body?.string()
            val jsonArray = JSONArray(responseBody)
            // check if there are no recipe instructions listed for the recipe
            if (jsonArray.length() == 0){
                fragment.activity?.runOnUiThread {
                    val recipeInstructionsMessage = fragment.view?.findViewById<TextView>(R.id.recipe_instructions_message)
                    if (recipeInstructionsMessage != null) {
                        recipeInstructionsMessage.isVisible = true
                        recipeInstructionsMessage.text = "We apologize, but we don't have instructions for the recipe at this time."
                    }
                }
            }
            else {
                val jsonObject = jsonArray.getJSONObject(0)
                val stepsArray = jsonObject.getJSONArray("steps")
                val recipeInstructionsList = mutableListOf<RecipeInstructions>()
                for (i in 0 until stepsArray.length()) {
                    val step = Gson().fromJson(stepsArray.getJSONObject(i).toString(), RecipeInstructions::class.java)
                    recipeInstructionsList.add(step)
                }
                fragment.activity?.runOnUiThread {
                    val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.recycler_view)
                    val layoutManager = LinearLayoutManager(fragment.context)
                    val adapter = RecipeInstructionsListAdapter(recipeInstructionsList)
                    if (recyclerView != null) {
                        recyclerView.layoutManager = layoutManager
                        recyclerView.adapter = adapter
                    }
                }
            }

        } else {
            Log.e("API_CALL", "Error: ${response.code}")
        }
    }

    override fun onFailure(call: okhttp3.Call, e: IOException) {
        // Handle failure
    }
}


}