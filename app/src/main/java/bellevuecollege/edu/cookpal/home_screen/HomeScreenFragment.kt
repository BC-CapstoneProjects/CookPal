package bellevuecollege.edu.cookpal.home_screen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.HomeScreenFragmentBinding
import bellevuecollege.edu.cookpal.profile.UserProfile
import bellevuecollege.edu.cookpal.profile.UserProfileHelper
import bellevuecollege.edu.cookpal.recipes.RecipeGridAdapter
import com.google.firebase.auth.FirebaseAuth

class HomeScreenFragment : Fragment() {

    companion object {
        fun newInstance() = HomeScreenFragment()
    }

    private val viewModel: HomeScreenViewModel by lazy {
        ViewModelProvider(this).get(HomeScreenViewModel::class.java)
    }

    private val up: UserProfile = UserProfile()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = HomeScreenFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel


        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recipesGrid.layoutManager = layoutManager

        //@TODO may need to add another adapter(?)
        binding.recipesGrid.adapter = RecipeGridAdapter(RecipeGridAdapter.OnClickListener {
            if (it.isLoadedSuccessful) {
                Toast.makeText(
                    activity,
                    "Loading recipe details. To be implemented.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(activity, "Failed to load", Toast.LENGTH_SHORT).show()
            }
        })

        //button listener for home screen to recipe search
        addButtonListener(binding.searchButton, R.id.action_homeScreen_to_recipeResults)

        //button listener for home screen to login screen
        addButtonListener(binding.profile, R.id.action_homeScreen_to_profile)

        //button listener for home screen to favorite recipes
        addButtonListener(binding.favoriteRecipeButton, R.id.action_homeScreen_to_favoriteRecipes)

        //button listener for home screen to upload recipe
        addButtonListener(binding.uploadRecipe, R.id.action_homeScreen_to_uploadRecipe)

        //button listener for popular button to recipe details
        addButtonListener(binding.popularButton, R.id.action_homeScreen_to_recipeDetails)

        when (FirebaseAuth.getInstance().currentUser) {
            null -> Handler(Looper.getMainLooper()).postDelayed({
                view?.findNavController()
                    ?.navigate(R.id.action_homeScreen_to_login)
            }, 50)
            else -> UserProfileHelper.loadProfile { data ->
                up.setProfile(data)
            }
        }

        return binding.root
    }

    private fun addButtonListener(button: ImageButton, navigationId: Int) {
        button.setOnClickListener { it.findNavController().navigate(navigationId) }
    }
}