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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import bellevuecollege.edu.cookpal.databinding.RecipeDetailsFragmentBinding
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.predictions.aws.AWSPredictionsPlugin
import java.io.*
import java.util.*

class RecipeDetailsFragment : Fragment() {
    private val UTTERANCE_ID: String = "tts1"
    private lateinit var mTTS: TextToSpeech
    private lateinit var recipeVoiceFile: File
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: RecipeDetailsFragmentBinding


    /**
     * AMPLIFY TEST
     */
    private val mp = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        convertTextToSpeech()
        // Setup Text To Speech engine
        mTTS = TextToSpeech(activity?.applicationContext,
            TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR){
                    //if there is no error then set language
                    mTTS.language = Locale.US
                }
            })
    }

    //Text to speech
    private fun convertTextToSpeech() {
        Amplify.Predictions.convertTextToSpeech("This actually works, nice",
            { playAudio(it.audioData) },
            { Log.e("MyAmplifyApp", "Failed to convert text to speech", it) }
        )
    }

    //Audio
    private fun playAudio(data: InputStream) {
        val cacheDir = requireActivity().externalCacheDir
        val mp3File = File(cacheDir, "audio.mp3")
        try {
            FileOutputStream(mp3File).use { out ->
                val buffer = ByteArray(8 * 1024)
                var bytesRead: Int
                while (data.read(buffer).also { bytesRead = it } != -1) {
                    out.write(buffer, 0, bytesRead)
                }
                mp.reset()
                mp.setOnPreparedListener { obj: MediaPlayer -> obj.start() }
                mp.setDataSource(FileInputStream(mp3File).fd)
                mp.prepareAsync()
            }
        } catch (error: IOException) {
            Log.e("MyAmplifyApp", "Error writing audio file.")
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
            this, viewModelFactory).get(RecipeDetailsViewModel::class.java)
        mediaPlayer = MediaPlayer()

        val tempView = binding.viewModel
        if (tempView != null) {

            tempView.selectedRecipe.observe(viewLifecycleOwner, Observer { parsedRecipe ->
                binding.recipeSummary.text = parsedRecipe.summary
                binding.recipeIngredients.text = parsedRecipe.ingredients
                binding.recipeInstructions.text = parsedRecipe.cookingInstructions
            })

            // Setup Record button handler
            binding.recordRecipeInstructionsButton.setOnClickListener {
                // Construct sound file
                recipeVoiceFile = File(
                    context?.cacheDir?.absolutePath,
                    tempView.selectedRecipe.value?.rId + ".wav"
                )
                if (recipeVoiceFile.exists()) {
                    recipeVoiceFile.delete()
                }
                val b = Bundle()
                val instructions = tempView.selectedRecipe.value?.cookingInstructions
                if (instructions != null) {
                    if (instructions.isNotEmpty()) {
                        // Save cooking instructions as a sound file, this may take time
                        mTTS.synthesizeToFile(instructions, b, recipeVoiceFile, UTTERANCE_ID)
                        mTTS.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                Log.d("Recipe Details Fragment", "Started synthesize To File")
                            }

                            override fun onDone(utteranceId: String?) {
                                if(utteranceId == UTTERANCE_ID) {
                                    Log.d("Recipe Details Fragment", "word is read, resuming with the next word")
                                    if (recipeVoiceFile.exists()) {
                                        mediaPlayer.setDataSource(recipeVoiceFile.absolutePath)
                                        mediaPlayer.prepare()
                                    }
                                }
                            }

                            override fun onError(utteranceId: String?) {
                                Log.e("Recipe Details Fragment", "Error synthesize to File")
                            }
                        })

                        // Enable related buttons
                        binding.deleteRecipeInstructionsButton.isEnabled = true
                        binding.playRecipeInstructionsButton.isEnabled = true

                    }
                }
            }
            // Setup Play button handler
            binding.playRecipeInstructionsButton.setOnClickListener {
                try {
                    if (recipeVoiceFile.exists()) {
                        mediaPlayer.start()
                        binding.pauseRecipeInstructionsButton.isEnabled = true
                    }
                }
                catch (e: Exception) {
                    Log.d("Recipe Details Fragment", "Error when playing audio")
                }
            }

            // Setup Speak button handler
            binding.speakRecipeInstructionsButton.setOnClickListener {
                val instructions = tempView.selectedRecipe.value?.cookingInstructions
                if (instructions != null) {
                    if (instructions.isNotEmpty()) {
                        mTTS.speak(instructions, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID)
                        binding.pauseRecipeInstructionsButton.isEnabled = true
                        Log.d("Recipe Details Fragment", "TTS successfully speak out recipe")
                    } else {
                        Log.e("Recipe Details Fragment", "No recipe instructions supplied for TTS")
                    }
                }
            }

            // Setup Pause button handler
            binding.pauseRecipeInstructionsButton.setOnClickListener {
                if (mTTS.isSpeaking){
                    //if speaking then Pause
                    mTTS.stop()
                }
                else if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
                else{
                    //if not speaking
                    Toast.makeText(activity, "Not speaking or playing recipe instructions", Toast.LENGTH_SHORT).show()
                }
            }

            // Setup Delete button handler
            binding.deleteRecipeInstructionsButton.setOnClickListener {
                if (recipeVoiceFile.exists()) {
                    recipeVoiceFile.delete()
                    binding.deleteRecipeInstructionsButton.isEnabled = false
                    binding.playRecipeInstructionsButton.isEnabled = false
                }
            }
        }

        return binding.root
    }

    override fun onPause() {
        mTTS.stop()
        if (mTTS.isSpeaking){
            //if speaking then Pause
            mTTS.stop()
        }
        else if (mediaPlayer.isPlaying) {
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