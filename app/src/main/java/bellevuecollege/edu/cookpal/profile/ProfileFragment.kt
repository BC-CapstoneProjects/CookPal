package bellevuecollege.edu.cookpal.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
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
    private val up: UserProfile = UserProfile()
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater)

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
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            choosePictureFromGallery.launch(intent)

        }

        var fbu: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()

        var username: String? = fbu?.email
        // binding.userNametext.text = username

        UserProfileHelper.loadProfile() { data ->

            up.setProfile(data)

            binding.name.setText(up.name)
            binding.emailAddress.setText(up.emailAddress)

            if (up.profilePhotoPath != "") {
                DownloadImageFromInternet(binding.imageView3).execute(up.profilePhotoPath)
            }
        }

        binding.updateProfile.setOnClickListener { view: View ->
            up.name = binding.name.text.toString()
            up.emailAddress = binding.emailAddress.text.toString()

            fbu?.updateEmail(up.emailAddress)

            UserProfileHelper.saveProfile(up)

            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0)


            Toast.makeText(
                getActivity(), "updated profile",
                Toast.LENGTH_SHORT
            ).show()

            view.findNavController().navigate(R.id.action_profileFragment_to_homeScreenFragment)

        }

        binding.micButton.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_profileFragment_to_selectVoiceFragment)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private lateinit var filePath: Uri

    private val choosePictureFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                filePath = it.data?.data!!

                var str = filePath.toString()


                try {
                    filePath?.let {
                        if (Build.VERSION.SDK_INT < 28) {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                this.activity?.contentResolver,
                                filePath
                            )
                            binding.imageView3.setImageBitmap(bitmap)
                        } else {
                            val source = this.activity?.let { it1 ->
                                ImageDecoder.createSource(
                                    it1.contentResolver,
                                    filePath
                                )
                            }
                            val bitmap = source?.let { it1 -> ImageDecoder.decodeBitmap(it1) }
                            binding.imageView3.setImageBitmap(bitmap)
                        }
                        uploadFileToFirebaseStorage()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                Log.d(TAG, "Fail to select image from Gallery")
            }
        }

    private fun uploadFileToFirebaseStorage() {
        if (filePath == null) return

        var fbu: FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()

        val filename = fbu?.uid
        val ref = FirebaseStorage.getInstance().getReference("/profile_photo/$filename")

        ref.putFile(filePath!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")

                    up.profilePhotoPath = it.toString()
                    UserProfileHelper.saveProfile(up)
                }
            }
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

}