package bellevuecollege.edu.cookpal.recipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import bellevuecollege.edu.cookpal.databinding.FragmentPhotoRecipeListBinding

/**
 * A simple [Fragment] subclass.
 * Use the [PhotoRecipeListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PhotoRecipeListFragment : Fragment() {

    private val viewModel: PhotoRecipeViewModel by lazy {
        ViewModelProvider(this).get(PhotoRecipeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPhotoRecipeListBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel
        binding.photoRecipesGrid.adapter = RecipeGridAdapter(RecipeGridAdapter.OnClickListener {
            // A meal is clicked, parse recipes and display info
            if (!it.isLoadedSuccessful) {
                Toast.makeText(activity, "Failed to load image, parsing recipe", Toast.LENGTH_SHORT).show()
            }
            viewModel.displayPhotoRecipeDetails(it)
        })

        viewModel.navigateToSelectedRecipe.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController().navigate(
                    PhotoRecipeListFragmentDirections.actionPhotoRecipeListFragmentToRecipeDetailsFragment(it)
                )
                viewModel.displayPhotoRecipeDetailsComplete()
            }
        })

        return binding.root
    }
}