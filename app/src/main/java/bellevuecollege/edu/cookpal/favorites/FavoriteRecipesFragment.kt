package bellevuecollege.edu.cookpal.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import bellevuecollege.edu.cookpal.databinding.FragmentFavoriteRecipesBinding
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.profile.UserProfile
import bellevuecollege.edu.cookpal.profile.UserProfileHelper
import bellevuecollege.edu.cookpal.recipe_parser.TAG

class FavoriteRecipesFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteRecipesBinding
    private val up: UserProfile = UserProfile()

    //private val _recipes = MutableLiveData<ArrayList<Recipe>>()
    var favoriteRecipes: ArrayList<Recipe> = ArrayList()

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
            Log.d(TAG, favoriteRecipes.toString())

//            val arrayAdapter: ArrayAdapter<*> =
//                ArrayAdapter(requireActivity().applicationContext, android.R.layout.simple_list_item_1, favoriteRecipes)
            val arrayAdapter = FavoriteRecipeAdapter(requireActivity().applicationContext, favoriteRecipes)
//            val arrayAdapter: FavoriteRecipeAdapter =
//                FavoriteRecipeAdapter(requireActivity().applicationContext, favoriteRecipes)
            binding.favoriteRecipeView.adapter = arrayAdapter
        }

        return binding.root
    }




}