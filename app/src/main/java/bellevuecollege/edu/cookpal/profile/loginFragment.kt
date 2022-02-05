package bellevuecollege.edu.cookpal.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.FragmentLoginBinding
import bellevuecollege.edu.cookpal.home_screen.HomeScreenViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentLoginBinding.inflate(inflater)

        binding.lifecycleOwner = this

        //button listener for loginFragment to signUpFragment
        binding.signUpButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        //button listener for loginFragment to profileFragment
        binding.loginButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
        }

        binding.forgotPassButton.setOnClickListener{ view: View ->
            view.findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

}