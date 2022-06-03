package bellevuecollege.edu.cookpal.home_screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.HomeScreenFragmentBinding
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.profile.UserProfile
import bellevuecollege.edu.cookpal.profile.UserProfileHelper
import bellevuecollege.edu.cookpal.recipes.RecipeGridAdapter
import com.google.firebase.auth.FirebaseAuth
import java.io.InputStream
import java.net.URL


class HomeScreenFragment : Fragment() {

    companion object {
        fun newInstance() = HomeScreenFragment()
    }

    private val viewModel: HomeScreenViewModel by lazy {
        ViewModelProvider(this).get(HomeScreenViewModel::class.java)
    }

    private val up: UserProfile = UserProfile()

    private val _navigateToSelectedRecipe = MutableLiveData<Recipe?>()
    val navigateToSelectedRecipe: MutableLiveData<Recipe?>
        get() = _navigateToSelectedRecipe

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
                /*Toast.makeText(
                    activity,
                    "Loading recipe details. To be implemented.",
                    Toast.LENGTH_SHORT
                ).show()*/

                displayRecipeDetails(it)
            } else {
                Toast.makeText(activity, "Failed to load", Toast.LENGTH_SHORT).show()
            }
        })

        //button listener for home screen to recipe search
        addButtonListener(binding.searchButton, R.id.action_homeScreen_to_recipeResults)

        //button listener for home screen to login screen
        addButtonListener(binding.profile, R.id.action_homeScreen_to_profile)

        // button listener for home screen to list photo recipes from Firebase storage
        binding.listPhotoRecipes.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(R.id.action_homeScreenFragment_to_photoRecipeListFragment)
        }

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

        binding.popularButton.setOnClickListener {
            var res = HomeScreenFragmentDirections.actionHomeScreenToRecipeDetails(viewModel.popularNow.value!!)

            this.findNavController().navigate(
                    res
            )

            displayRecipeDetailsComplete()
        }

        viewModel.popularNow.observe(viewLifecycleOwner) {

            try{
                if (null != it) {
                    binding.popularName.text = it.title

                    DownloadImageFromInternet(binding.popularImage).execute(it.imageUrl)
                }
            }
            catch (e:Exception){

            }

        }

        navigateToSelectedRecipe.observe(viewLifecycleOwner) {

            if (null != it) {

                var res = HomeScreenFragmentDirections.actionHomeScreenToRecipeDetails(it)

                this.findNavController().navigate(
                        res
                        )



                displayRecipeDetailsComplete()
            }
        }

        return binding.root
    }

    @SuppressLint("StaticFieldLeak")
    @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) :
            AsyncTask<String, Void, Bitmap?>() {
        init {
            Toast.makeText(
                    getActivity(),
                    "Please wait, it may take a few minute...",
                    Toast.LENGTH_SHORT
            ).show()
        }

        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }

        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }

    fun displayRecipeDetails(recipe: Recipe) {
        _navigateToSelectedRecipe.value = recipe
    }

    fun displayRecipeDetailsComplete() {
        _navigateToSelectedRecipe.value = null
    }

    private fun addButtonListener(button: ImageButton, navigationId: Int) {
        button.setOnClickListener { it.findNavController().navigate(navigationId) }
    }
}