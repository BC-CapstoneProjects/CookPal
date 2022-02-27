package bellevuecollege.edu.cookpal.profile

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.FragmentLoginBinding
import bellevuecollege.edu.cookpal.home_screen.HomeScreenFragment
import bellevuecollege.edu.cookpal.home_screen.HomeScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var auth: FirebaseAuth
    private var TAG = "EmailPassword"
    private lateinit var viewModel: LoginFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentLoginBinding.inflate(inflater)

        binding.lifecycleOwner = this
        auth = Firebase.auth

        //button listener for loginFragment to signUpFragment
        binding.signUpButton.setOnClickListener { view: View ->

            var emailAdd : String = binding.emailAddress.text.toString()
            var pwd : String = binding.password.text.toString()
            createAccount(emailAdd, pwd)

            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)

            binding.emailAddress.setText("")
            binding.password.setText("")
          //  view.findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        //button listener for loginFragment to profileFragment
        binding.loginButton.setOnClickListener { view: View ->

            var emailAdd : String = binding.emailAddress.text.toString()
            var pwd : String = binding.password.text.toString()

            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)

            binding.emailAddress.setText("")
            binding.password.setText("")

            signIn(emailAdd, pwd)
            //view.findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
        }

        binding.forgotPassButton.setOnClickListener{ view: View ->
            view.findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]

        var at : Activity = getContext() as Activity;

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(at, { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    
                    view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeScreenFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(getActivity(), "email or password is incorrect",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            })
        // [END sign_in_with_email]
    }

    private fun createAccount(email: String, password: String) {

        var at : Activity = getContext() as Activity;

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(at) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(getActivity(), "createAccount Authentication success.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(user)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(getActivity(), "createAccount Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }
}