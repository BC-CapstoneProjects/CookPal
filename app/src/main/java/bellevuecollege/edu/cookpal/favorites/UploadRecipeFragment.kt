package bellevuecollege.edu.cookpal.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.FragmentUploadRecipeBinding
import bellevuecollege.edu.cookpal.profile.LoginFragment


/**
 * A simple [Fragment] subclass.
 * Use the [UploadRecipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadRecipeFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: UploadRecipeFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentUploadRecipeBinding.inflate(inflater)

        binding.lifecycleOwner = this

        //button listener for upload recipe button to upload photo
        binding.uploadRecipeImageButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate(R.id.action_uploadRecipeFragment_to_uploadPhotoFragment)
        }

        // Inflate the layout for this fragment
        return binding.root
    }


}