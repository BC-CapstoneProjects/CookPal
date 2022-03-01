package bellevuecollege.edu.cookpal.profile

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.findNavController
import bellevuecollege.edu.cookpal.databinding.FragmentProfileBinding
import bellevuecollege.edu.cookpal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_profile.*


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
    private val up:UserProfile = UserProfile()

    val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView3.setImageBitmap(imageBitmap)
val dt = data.data
var t = 0
        }
    }

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

        binding.takepicProfile.setOnClickListener { view: View ->
            dispatchTakePictureIntent()
        }

        var fbu : FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()

        var username : String? = fbu?.email
       // binding.userNametext.text = username

        UserProfileHelper.loadProfile() { data ->

            up.setProfile(data)

            binding.name.setText(up.name)
            binding.emailAddress.setText(up.emailAddress)
        }

        binding.updateProfile.setOnClickListener{ view : View ->
            up.name = binding.name.text.toString()
            up.emailAddress = binding.emailAddress.text.toString()

            fbu?.updateEmail(up.emailAddress)

            UserProfileHelper.saveProfile(up)

            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)


            Toast.makeText(getActivity(), "updated profile",
                Toast.LENGTH_SHORT).show()

            view.findNavController().navigate(R.id.action_profileFragment_to_homeScreenFragment)

        }

        binding.micButton.setOnClickListener{ view : View ->
            view.findNavController().navigate(R.id.action_profileFragment_to_selectVoiceFragment)
        }


        // Inflate the layout for this fragment
        return binding.root
    }

}