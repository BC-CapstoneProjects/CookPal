package bellevuecollege.edu.cookpal.recipes

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bellevuecollege.edu.cookpal.databinding.RecipeResultsFragmentBinding

class RecipeResultsFragment : Fragment() {

    private val viewModel: RecipeResultsViewModel by lazy {
        ViewModelProvider(this).get(RecipeResultsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = RecipeResultsFragmentBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel
        binding.recipesGrid.adapter = RecipeGridAdapter(RecipeGridAdapter.OnClickListener {
            if (it.isLoadedSuccessful) {
                Toast.makeText(activity, "Loading recipe details. To be implemented.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Failed to load", Toast.LENGTH_SHORT).show()
            }
        })

        // EditText handler
        binding.searchBox.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                viewModel.setSearchTerm(s.toString())
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
        return binding.root
    }
}