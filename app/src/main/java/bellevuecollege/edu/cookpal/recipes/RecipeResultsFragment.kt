package bellevuecollege.edu.cookpal.recipes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import bellevuecollege.edu.cookpal.databinding.RecipeResultsFragmentBinding
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*


class RecipeResultsFragment : Fragment() {

    private var mSocket: Socket? = null

    private val viewModel: RecipeResultsViewModel by lazy {
        ViewModelProvider(this).get(RecipeResultsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try {
            mSocket = IO.socket("http://10.0.0.167:3000")
            mSocket?.connect()
        } catch (e: URISyntaxException) {

        }

        val uuid:String = UUID.randomUUID().toString()

        mSocket?.emit("connecttoserverandroid", uuid)

        mSocket?.let { viewModel.setSocketObject(it, uuid) }



        val binding = RecipeResultsFragmentBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel
        binding.recipesGrid.adapter = RecipeGridAdapter(RecipeGridAdapter.OnClickListener {
            // A meal is clicked, parse recipes and display info
            if (!it.isLoadedSuccessful) {
                Toast.makeText(activity, "Failed to load image, parsing recipe", Toast.LENGTH_SHORT).show()
            }
            viewModel.displayRecipeDetails(it)
        })

        viewModel.navigateToSelectedRecipe.observe(viewLifecycleOwner) {
            if (null != it) {
                this.findNavController().navigate(
                    RecipeResultsFragmentDirections.actionRecipeResultsToRecipeDetails(
                        it
                    )
                )
                viewModel.displayRecipeDetailsComplete()
            }
        }

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

    override fun onDestroy() {

        super.onDestroy()

        mSocket?.disconnect();
    }
}