package bellevuecollege.edu.cookpal.home_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.HomeScreenFragmentBinding
import bellevuecollege.edu.cookpal.recipes.RecipeResultsViewModel

import androidx.recyclerview.widget.LinearLayoutManager
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.recipes.RecipeGridAdapter
import kotlinx.android.synthetic.main.home_screen_fragment.*

class HomeScreenFragment : Fragment() {

    companion object {
        fun newInstance() = HomeScreenFragment()
    }

    private val viewModel: HomeScreenViewModel by lazy {
        ViewModelProvider(this).get(HomeScreenViewModel::class.java)
    }

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
                Toast.makeText(activity, "Loading recipe details. To be implemented.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Failed to load", Toast.LENGTH_SHORT).show()
            }
        })

        //button listener for home screen to recipe search
        binding.searchButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_homeScreenFragment_to_recipeResultsFragment)
        }
        //button listener for home screen to login screen
        binding.profile.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_homeScreenFragment_to_loginFragment)
        }
        //button listener for home screen to favorite recipes
        binding.favoriteRecipeButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_homeScreenFragment_to_favoriteRecipesFragment)
        }
        //button listener for home screen to upload recipe
        binding.uploadRecipe.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_homeScreenFragment_to_uploadRecipeFragment)
        }
        binding.RiceButtton.setOnClickListener {
            RiceButtton.setImageResource(R.drawable.ic_new_rice_button2)
        }
        return binding.root
    }
}