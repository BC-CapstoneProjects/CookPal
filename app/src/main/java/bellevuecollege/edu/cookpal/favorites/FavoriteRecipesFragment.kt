package bellevuecollege.edu.cookpal.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import bellevuecollege.edu.cookpal.databinding.FragmentFavoriteRecipesBinding
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.profile.UserProfile
import bellevuecollege.edu.cookpal.profile.UserProfileHelper
import bellevuecollege.edu.cookpal.recipe_parser.TAG
import com.google.common.collect.ImmutableList
import com.google.gson.Gson

class FavoriteRecipesFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteRecipesBinding
    private val up: UserProfile = UserProfile()

    //private val _recipes = MutableLiveData<ArrayList<Recipe>>()
    lateinit var favoriteRecipes: ArrayList<Recipe>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteRecipesBinding.inflate(inflater)
        binding.lifecycleOwner = this

        //Load profile
        UserProfileHelper.loadProfile { data ->
            up.setProfile(data)
            favoriteRecipes = up.favoriteRecipes

//            val arrayAdapter: ArrayAdapter<*> =
//                ArrayAdapter(requireActivity().applicationContext, android.R.layout.simple_list_item_1, favoriteRecipes)
            val arrayAdapter = FavoriteRecipeAdapter(requireActivity().applicationContext, favoriteRecipes)
//            val arrayAdapter: FavoriteRecipeAdapter =
//                FavoriteRecipeAdapter(requireActivity().applicationContext, favoriteRecipes)
            binding.favoriteRecipeView.adapter = arrayAdapter

            var listView = binding.favoriteRecipeView
            listView.setOnItemClickListener { parent, view, position, id ->

                //val asString = up.favoriteRecipes[position].toString()
                val map : HashMap<String, Any> = listView.getItemAtPosition(position) as HashMap<String, Any>

                val selectedRecipe : Recipe = Recipe()

                selectedRecipe.summary = map["summary"] as String
                selectedRecipe.totalTime = map["totalTime"] as String
                selectedRecipe.rating = map["rating"] as String
                selectedRecipe.label = map["label"] as String
                if (map["rId"] != null)
                    selectedRecipe.rId = map["rId"] as String
                selectedRecipe.title = map["title"] as String
                selectedRecipe.steps = map["steps"] as ArrayList<String>
                selectedRecipe.sourceUrl = map["sourceUrl"] as String
                selectedRecipe.imageUrl = map["imageUrl"] as String
                selectedRecipe.isLoadedSuccessful = map["loadedSuccessful"] as Boolean
                selectedRecipe.ingredients = map["ingredients"] as ArrayList<String>

                selectedRecipe.reviewNumber = map["reviewNumber"].toString().toInt()
                selectedRecipe._id = map["_id"] as String
                selectedRecipe.id = map["id"] as String


                //Log.d("Map Values", selectedRecipe.toString())
            }
        }

        return binding.root
    }

}

