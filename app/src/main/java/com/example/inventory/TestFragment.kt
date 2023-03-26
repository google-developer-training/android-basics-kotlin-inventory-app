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
 * Use the [TestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TestFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var adapter : MyAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipesArrayList : ArrayList<myRecipe>

    lateinit var imageId : Array<Int>
    lateinit var heading : Array<String>
    lateinit var recipes : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recipe_list_fragment, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = MyAdapter(recipesArrayList)
        recyclerView.adapter = adapter
    }



//    private fun getRecipes() {
//        val url = "https://api.spoonacular.com/recipes/findByIngredients?apiKey=c08a9abc204a46908523eeddcf170c27&ingredients=apples,+flour,+sugar"
//        val apiService = MyApiService()
//        apiService.makeApiRequest(url, MyCallback())
//    }
//
//    class TTApiService {
//        fun makeApiRequest(url: String, callback: Callback) {
//            val client = OkHttpClient()
//            val request = Request.Builder()
//                .url(url)
//                .build()
//            client.newCall(request).enqueue(callback)
//        }
//    }
//
//
//    class TTCallback : Callback {
//        override fun onResponse(call: okhttp3.Call, response: Response) {
//            val responseBody = response.body?.string()
//            val recipeList = Gson().fromJson(responseBody, Array<Recipe>::class.java)
//            recipeList.forEachIndexed { _, recipe ->
//                println("Recipe: ${recipe.title}")
//            }
//        }
//        override fun onFailure(call: okhttp3.Call, e: IOException) {
//            // Handle failure
//        }
//
//}








private fun dataInitialize() {
        recipesArrayList = arrayListOf<myRecipe>()


//        not used currently, but for onClickListener (maybe to open a details page)
        // this could be used. according to FA
        imageId = arrayOf(
            R.drawable.cookie,
            R.drawable.cookie2,
            R.drawable.cookie3,
            R.drawable.cookie4,
        )

        heading = arrayOf(
            "Recipe1",
            "Recipe2",
            "Recipe3",
            "Recipe4",
        )

        for (i in imageId.indices){
            val recipe = myRecipe(imageId[i], heading[i])
            recipesArrayList.add(recipe)
        }
    }
}