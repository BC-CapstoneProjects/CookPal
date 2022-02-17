package bellevuecollege.edu.cookpal.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import bellevuecollege.edu.cookpal.databinding.FragmentProfileBinding
import bellevuecollege.edu.cookpal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


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


        binding.profilePicture.setOnClickListener { view: View ->
         //   view.findNavController().navigate(R.id.act)

            FirebaseAuth.getInstance().signOut()
            view.findNavController().navigate(R.id.action_profileFragment_to_loginFragment)

        }

        var fbu : FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()

        var username : String? = fbu?.email
        binding.userNametext.text = username


        binding.micButton.setOnClickListener{ view : View ->
            view.findNavController().navigate(R.id.action_profileFragment_to_selectVoiceFragment)
        }


        // Inflate the layout for this fragment
        return binding.root
    }

}