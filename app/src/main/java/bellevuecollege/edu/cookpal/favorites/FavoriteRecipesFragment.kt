package bellevuecollege.edu.cookpal.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import bellevuecollege.edu.cookpal.databinding.FragmentFavoriteRecipesBinding
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.profile.UserProfile
import bellevuecollege.edu.cookpal.profile.UserProfileHelper
import bellevuecollege.edu.cookpal.recipe_parser.TAG
import com.google.common.collect.ImmutableList
import com.google.gson.Gson
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory

class FavoriteRecipesFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteRecipesBinding
    private val up: UserProfile = UserProfile()

    //private val _recipes = MutableLiveData<ArrayList<Recipe>>()
    lateinit var favoriteRecipes: ArrayList<Recipe>

    private val viewModel: FavoriteRecipesViewModel by lazy {
        ViewModelProvider(this).get(FavoriteRecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteRecipesBinding.inflate(inflater)
        binding.lifecycleOwner = this

        //Load profile
        setArrayAdapter()

        var selectedRecipe: Recipe
        binding.favoriteRecipeView.setOnItemClickListener { parent, view, position, id ->
            val map : HashMap<String, Any> = binding.favoriteRecipeView.getItemAtPosition(position) as HashMap<String, Any>
            selectedRecipe = mapToRecipe(map)
            navigateToSelected(selectedRecipe)
            }

        return binding.root
    }

    private fun setArrayAdapter() {
        UserProfileHelper.loadProfile { data ->
            up.setProfile(data)
            favoriteRecipes = up.favoriteRecipes

            val arrayAdapter =
                FavoriteRecipeAdapter(requireActivity().applicationContext, favoriteRecipes)
            binding.favoriteRecipeView.adapter = arrayAdapter
        }
    }

    private fun navigateToSelected(selectedRecipe: Recipe){
        viewModel.displayRecipeDetails(selectedRecipe)
        viewModel.navigateToSelectedRecipe.observe(viewLifecycleOwner) {
            if(null != it){
                view?.findNavController()?.navigate(
                    FavoriteRecipesFragmentDirections.
                    actionFavoriteRecipesFragmentToRecipeDetailsFragment(it)
                )
                viewModel.displayRecipeDetailsComplete()
            }
        }
    }

    private fun mapToRecipe(recipeMap : HashMap<String, Any>) : Recipe{
        var result = Recipe()

        result.summary = recipeMap["summary"] as String
        result.totalTime = recipeMap["totalTime"] as String
        result.rating = recipeMap["rating"] as String
        result.label = recipeMap["label"] as String
        if (recipeMap["rId"] != null)
            result.rId = recipeMap["rId"] as String
        result.title = recipeMap["title"] as String
        result.steps = recipeMap["steps"] as ArrayList<String>
        result.sourceUrl = recipeMap["sourceUrl"] as String
        result.imageUrl = recipeMap["imageUrl"] as String
        result.isLoadedSuccessful = recipeMap["loadedSuccessful"] as Boolean
        result.ingredients = recipeMap["ingredients"] as ArrayList<String>
        result.reviewNumber = recipeMap["reviewNumber"].toString().toInt()
        result._id = recipeMap["_id"] as String
        result.id = recipeMap["id"] as String

        return result
    }
}

