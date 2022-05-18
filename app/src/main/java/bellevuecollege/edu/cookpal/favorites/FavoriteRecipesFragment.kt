package bellevuecollege.edu.cookpal.favorites

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.FragmentFavoriteRecipesBinding
import bellevuecollege.edu.cookpal.profile.UserProfile
import bellevuecollege.edu.cookpal.profile.UserProfileHelper

class FavoriteRecipesFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteRecipesBinding
    private val up: UserProfile = UserProfile()

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
            Log.d("FavoriteRecipeFragment Name", up.name)
            Log.d("FavoriteRecipeFragment favorites", up.favoriteRecipes.toString())
            Log.d("FavoriteRecipeFragment tempFR", up.tempFR.toString())
        }
        return binding.root
    }


}