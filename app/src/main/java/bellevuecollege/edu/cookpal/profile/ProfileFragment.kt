package bellevuecollege.edu.cookpal.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import bellevuecollege.edu.cookpal.databinding.FragmentProfileBinding
import bellevuecollege.edu.cookpal.R


/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: ProfileFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentProfileBinding.inflate(inflater)

        binding.lifecycleOwner = this

        //button listener for profile to change password
        binding.changePassButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

}