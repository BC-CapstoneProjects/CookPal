package bellevuecollege.edu.cookpal.home_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.HomeScreenFragmentBinding

class HomeScreenFragment : Fragment() {

    companion object {
        fun newInstance() = HomeScreenFragment()
    }

    private lateinit var viewModel: HomeScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = HomeScreenFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.searchButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_homeScreenFragment_to_recipeResultsFragment)
        }
        return binding.root
    }
}