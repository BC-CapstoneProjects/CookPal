package bellevuecollege.edu.cookpal.recipe_details

import android.R
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import bellevuecollege.edu.cookpal.databinding.RecipeDetailsFragmentBinding
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.profile.UserProfile
import bellevuecollege.edu.cookpal.profile.UserProfileHelper
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.io.File
import java.util.*
import android.widget.Spinner

class RecipeDetailsFragment : Fragment() {
    private val UTTERANCE_ID: String = "tts1"
    private lateinit var mTTS: TextToSpeech
    private lateinit var recipeVoiceFile: File
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: RecipeDetailsFragmentBinding
    private val up: UserProfile = UserProfile()
    private lateinit var recipe : Recipe
    private var modelDownloaded: Boolean = false //flag to check if translator model has been downloaded
    private lateinit var engJapTranslator: Translator
    private val espan: Locale = Locale("es","es")
    private val lang: Array<String> = arrayOf("English", "Spanish", "Japanese")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup Text To Speech engine
        mTTS = TextToSpeech(
            activity?.applicationContext
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                //if there is no error then set language
                //mTTS.language = Locale.JAPANESE
                mTTS.language = espan
            }
        }
        /**
         * Set up translator model
         */
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()
        engJapTranslator = Translation.getClient(options)
        var conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        //download language model
        //Language models are around 30MB, don't have too many.
        engJapTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating
                // Set a flag, unhide the translation UI, etc.
                Log.d("Translator", "Model download successful")
                modelDownloaded = true
            }
            .addOnFailureListener {
                //Model couldn't be downloaded or other internal error
                Log.e("Translator", "Model download failed")
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(activity).application
        binding = RecipeDetailsFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = this
        val recipeDetails = RecipeDetailsFragmentArgs.fromBundle(requireArguments()).selectedRecipe
        val viewModelFactory = RecipeDetailsViewModelProvider(recipeDetails, application)
        binding.viewModel = ViewModelProvider(
            this, viewModelFactory
        ).get(RecipeDetailsViewModel::class.java)
        mediaPlayer = MediaPlayer()

        UserProfileHelper.loadProfile { data ->
            up.setProfile(data)
        }

        //View model
        val tempView = binding.viewModel
        //Get selected recipe information and place into xml
        if (tempView != null) {
            tempView.selectedRecipe.observe(viewLifecycleOwner) { parsedRecipe ->
                recipe = parsedRecipe
                //binding.recipeSummary.text = parsedRecipe.summary
                binding.recipeIngredients.text =
                    parsedRecipe.ingredients.joinToString("") { "- $it\n" }
                binding.recipeInstructions.text = parsedRecipe.steps.mapIndexed{index, s -> "${index+1}) $s" }.joinToString("") { "$it\n" }
            }
        }

        //Array adapter for spinner/drop down menu
        val aa = ArrayAdapter(requireActivity().applicationContext, R.layout.simple_spinner_item, lang)
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        val spinner = binding.langSpinner
        spinner.adapter = aa

        binding.addFavorite.setOnClickListener {

            up.favoriteRecipes.add(recipe)
            UserProfileHelper.saveProfile(up)
        }
        // Setup Speak button handler
        binding.speakRecipeInstructionsButton.setOnClickListener {
            if (tempView != null) {
                val instructions = recipe.steps.mapIndexed{index, s -> "${index+1}) $s" }.joinToString("") { "$it\n" }

                if (instructions != null) {
                    if (modelDownloaded) {
                        //translate english instructions to language
                        engJapTranslator.translate(instructions)
                            .addOnSuccessListener {
                                Log.d("Translated text", it) //it refers to the translated string
                                mTTS.speak(it, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID)
                                binding.pauseRecipeInstructionsButton.isEnabled = true
                                Log.d("Recipe Details Fragment", "TTS successfully speak out recipe")
                            }
                            .addOnFailureListener {
                                Log.e("Translated text", "Failed to translate")
                            }
                    }
                } else {
                    Log.e(
                        "Recipe Details Fragment",
                        "No recipe instructions supplied for TTS"
                    )
                }

                /**
                 * Old TTS code
                 */
//                Log.d("-----instructions-----", instructions)
//                if (instructions != null) {
//                    if (instructions.isNotEmpty()) {
//                        mTTS.speak(instructions, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID)
//                        binding.pauseRecipeInstructionsButton.isEnabled = true
//                        Log.d("Recipe Details Fragment", "TTS successfully speak out recipe")
//                    } else {
//                        Log.e(
//                            "Recipe Details Fragment",
//                            "No recipe instructions supplied for TTS"
//                        )
//                    }
//                }
            }
        }

        // Setup Pause button handler
        binding.pauseRecipeInstructionsButton.setOnClickListener {
            if (mTTS.isSpeaking) {
                //if speaking then Pause
                mTTS.stop()
            } else if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
            } else {
                    //if not speaking
                    Toast.makeText(
                        activity,
                        "Not speaking or playing recipe instructions",
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
        return binding.root
    }

    override fun onPause() {
        mTTS.stop()
        if (mTTS.isSpeaking) {
            //if speaking then Pause
            mTTS.stop()
        } else if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        super.onPause()
    }

    override fun onDestroy() {
        mTTS.shutdown()
        mediaPlayer.stop()
        if (this::recipeVoiceFile.isInitialized) {
            recipeVoiceFile.delete()
        }
        super.onDestroy()
    }
}