package com.example.inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



/**
 * A simple [Fragment] subclass.
 * Use the [RecipeListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecipeListFragment : Fragment() {

    private lateinit var adapter : RecipeListAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeList : Array<Recipe>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recipe_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getRecipes()
    }

    private fun getRecipes() {
        val url = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=c08a9abc204a46908523eeddcf170c27&ingredients=apples,+flour,+sugar&number=1"
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

    class MyCallback(private val fragment: RecipeListFragment) : Callback {
        override fun onResponse(call: okhttp3.Call, response: Response) {
            val responseBody = response.body?.string()
            val recipeList = Gson().fromJson(responseBody, Array<Recipe>::class.java)
            fragment.activity?.runOnUiThread {
                val recyclerView = fragment.view?.findViewById<RecyclerView>(R.id.recycler_view)
                val layoutManager = LinearLayoutManager(fragment.context)
                val adapter = RecipeListAdapter(recipeList)
                if (recyclerView != null) {
                    recyclerView.layoutManager = layoutManager
                    recyclerView.setHasFixedSize(true)
                    recyclerView.adapter = adapter
                }
            }
        }

        override fun onFailure(call: okhttp3.Call, e: IOException) {
            // Handle failure
        }
    }

}
