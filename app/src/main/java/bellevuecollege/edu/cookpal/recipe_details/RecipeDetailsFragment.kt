package bellevuecollege.edu.cookpal.recipe_details

import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import bellevuecollege.edu.cookpal.databinding.RecipeDetailsFragmentBinding
import java.io.File
import java.util.*

class RecipeDetailsFragment : Fragment() {
    private val UTTERANCE_ID: String = "tts1"
    private lateinit var mTTS: TextToSpeech
    private lateinit var recipeVoiceFile: File
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: RecipeDetailsFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup Text To Speech engine
        mTTS = TextToSpeech(
            activity?.applicationContext
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                //if there is no error then set language
                mTTS.language = Locale.US
            }
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

        val tempView = binding.viewModel
        if (tempView != null) {

            tempView.selectedRecipe.observe(viewLifecycleOwner) { parsedRecipe ->
                binding.recipeSummary.text = parsedRecipe.summary
                binding.recipeIngredients.text =
                    parsedRecipe.ingredients.joinToString("") { "- $it\n" }
                binding.recipeInstructions.text = parsedRecipe.steps.mapIndexed{index, s -> "${index+1}) $s" }.joinToString("") { "$it\n" }
            }

            // Setup Speak button handler
            binding.speakRecipeInstructionsButton.setOnClickListener {
                if (tempView != null) {
                    val instructions = tempView.selectedRecipe.value?.steps
                    if (instructions != null) {
                        if (instructions.isNotEmpty()) {
                            mTTS.speak(instructions[0], TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID)
                            binding.pauseRecipeInstructionsButton.isEnabled = true
                            Log.d("Recipe Details Fragment", "TTS successfully speak out recipe")
                        } else {
                            Log.e(
                                "Recipe Details Fragment",
                                "No recipe instructions supplied for TTS"
                            )
                        }
                    }
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