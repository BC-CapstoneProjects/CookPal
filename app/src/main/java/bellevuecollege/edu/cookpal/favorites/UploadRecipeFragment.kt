package bellevuecollege.edu.cookpal.favorites

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import bellevuecollege.edu.cookpal.databinding.FragmentUploadRecipeBinding
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.profile.LoginFragment
import bellevuecollege.edu.cookpal.profile.UserProfile
import bellevuecollege.edu.cookpal.profile.UserProfileHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [UploadRecipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadRecipeFragment : Fragment() {
    private val TAG = "UploadRecipeFragment"
    private lateinit var filePath: Uri
    private lateinit var binding: FragmentUploadRecipeBinding
    private lateinit var bitmap: Bitmap
    private var recipe : Recipe = Recipe()
    private var emptyRecipe: Recipe = Recipe()
    private val up: UserProfile = UserProfile()

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

        UserProfileHelper.loadProfile { data ->
            up.setProfile(data)
        }
        // Confirm upload recipe button listener
        binding.confirmUploadRecipe.setOnClickListener { view: View ->
            uploadFileToFirebaseStorage()
        }

        // Upload recipe from gallery listener
        binding.uploadFromImageGallery.setOnClickListener { view: View ->
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            chooseRecipeFromGallery.launch(intent)
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    // Receiver for recipe image
    private val choosePictureFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            selectFromGallery(it)
            binding.selectRecipeImage.setImageBitmap(bitmap)
        }

    // Receiver for recipe
    private val chooseRecipeFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            selectFromGallery(it)
            //Create input image for text recognition from path file
            val image: InputImage
            try {
                image = InputImage.fromFilePath(requireActivity().applicationContext, filePath)
                recognizeText(image)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    /**
     * Updates filepath and bitmap
     * to selected picture from gallery
     */
    private fun selectFromGallery(result: ActivityResult){
        if (result.resultCode == Activity.RESULT_OK) {
            filePath = result.data?.data!!

            try {
                filePath?.let {
                    if (Build.VERSION.SDK_INT < 31) {
                        bitmap = MediaStore.Images.Media.getBitmap(
                            this.activity?.contentResolver,
                            filePath
                        )
                    } else {
                        val source = this.activity?.let { it1 ->
                            ImageDecoder.createSource(
                                it1.contentResolver,
                                filePath
                            )
                        }
                        bitmap = source?.let { it1 -> ImageDecoder.decodeBitmap(it1) }!!
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
                Log.d("Upload Recipe Fragment success listener", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                    recipe.imageUrl = it.toString()
                    up.favoriteRecipes.add(recipe)
                    UserProfileHelper.saveProfile(up)
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
        Log.d("Upload Recipe Fragment saveUserToFirebaseDatabase", "File Location: $profileImageUrl")
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

    //Function to recognize text from image
    private fun recognizeText(image: InputImage) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                Log.d("Image text", visionText.text) //print to log entire text
                var name = ""
                var ingre = ""
                var instr = ""

                /**
                 * flag is used to determine which string to concatenate to
                 * flag = 0 is name
                 * flag = 1 is ingredients
                 * flag = 2 is instructions
                 */
                var flag = 0

                //Concatenate to string
                for(block in visionText.textBlocks)
                {
                    //Change flag state when "ingredients" is found
                    if (block.text.startsWith("Ingredients"))
                        flag = 1
                    //Change flag state when "instructions" is found
                    if (block.text.startsWith("Instructions"))
                        flag = 2
                    //Concatenate to name
                    if (flag == 0)
                        name += block.text + " "
                    //Concatenate to ingredients
                    if (flag == 1)
                        ingre += block.text + "\n"
                    //Concatenate to instructions
                    if (flag == 2)
                        instr += block.text + "\n"
                }

                recipe.title = name
                var ingredientsArray = ArrayList<String>()
                for (i in ingre.lines()) {
                    ingredientsArray.add(i)
                }
                recipe.ingredients = ingredientsArray
                var stepsArray = ArrayList<String>()
                for (i in instr.lines())
                {
                    stepsArray.add(i)
                }
                recipe.steps = stepsArray
                //Change text on view
                binding.photoRecipeName.setText(name)
                binding.recipeIngredients.setText(ingre)
                binding.recipeInstructions.setText(instr)
            }
            .addOnFailureListener {
                Log.e("Recognize Text", "Recognize Text Failed")
            }
    }
}

class PhotoRecipe(
    val filePath: String, val name: String, val summary: String,
    val instructions: String, val ingredients: String
) {
    constructor() : this("", "", "", "", "") {}
}