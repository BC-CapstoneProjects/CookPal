package bellevuecollege.edu.cookpal.favorites

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import bellevuecollege.edu.cookpal.databinding.FragmentUploadRecipeBinding
import bellevuecollege.edu.cookpal.profile.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [UploadRecipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadRecipeFragment : Fragment() {
    private val TAG = "UploadRecipeFragment"
    private lateinit var filePath: Uri
    private lateinit var binding: FragmentUploadRecipeBinding

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: UploadRecipeFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUploadRecipeBinding.inflate(inflater)

        binding.lifecycleOwner = this

        // Select recipe button listener to select Gallery image
        binding.selectRecipeImage.setOnClickListener { view: View ->
            Log.d(TAG, "Try to select a recipe photo from Gallery")
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            choosePictureFromGallery.launch(intent)
        }

        // Confirm upload recipe button listener
        binding.confirmUploadRecipe.setOnClickListener { view: View ->
            Log.d(TAG, "Try to upload a photo recipe to Firebase Storage")
            uploadFileToFirebaseStorage()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    // Receiver
    private val choosePictureFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                filePath = it.data?.data!!

                try {
                    filePath?.let {
                        if (Build.VERSION.SDK_INT < 28) {
                            val bitmap = MediaStore.Images.Media.getBitmap(
                                this.activity?.contentResolver,
                                filePath
                            )
                            binding.selectRecipeImage.setImageBitmap(bitmap)
                        } else {
                            val source = this.activity?.let { it1 ->
                                ImageDecoder.createSource(
                                    it1.contentResolver,
                                    filePath
                                )
                            }
                            val bitmap = source?.let { it1 -> ImageDecoder.decodeBitmap(it1) }
                            binding.selectRecipeImage.setImageBitmap(bitmap)
                        }
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

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/recipe_photos/$filename")

        binding.uploadProgressBar.visibility = View.INVISIBLE
        binding.uploadProgressText.visibility = View.INVISIBLE

        ref.putFile(filePath!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                    binding.uploadProgressBar.visibility = View.INVISIBLE
                    binding.uploadProgressText.visibility = View.INVISIBLE
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
                binding.uploadProgressBar.visibility = View.INVISIBLE
                binding.uploadProgressText.visibility = View.INVISIBLE
            }
            .addOnProgressListener {
                binding.uploadProgressBar.visibility = View.VISIBLE
                binding.uploadProgressText.visibility = View.VISIBLE

                var progress: Double = (100.0 * it.bytesTransferred) / it.totalByteCount
                binding.uploadProgressBar.progress = progress.toInt()

                var progressString: String = progress.toInt().toString() + " %"
                binding.uploadProgressText.text = progressString
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/recipe_data")

        val photoRecipe = PhotoRecipe(
            profileImageUrl,
            binding.photoRecipeName.text.toString(),
            binding.photoRecipeSummary.text.toString(),
            binding.recipeInstructions.text.toString(),
            binding.recipeIngredients.text.toString()
        )

        ref.push().setValue(photoRecipe)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully saved photo recipe to Firebase Database")
            }
            .addOnFailureListener {
                Log.d(
                    TAG,
                    "Failed to upload photo recipe to Firebase Database. Error: ${it.message}"
                )
            }
    }
}

class PhotoRecipe(
    val filePath: String, val name: String, val summary: String,
    val instructions: String, val ingredients: String
) {
    constructor() : this("", "", "", "", "") {}
}