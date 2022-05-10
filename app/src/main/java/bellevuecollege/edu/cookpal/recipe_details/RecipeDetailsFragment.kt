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
import android.widget.AdapterView
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

class RecipeDetailsFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private val UTTERANCE_ID: String = "tts1"
    private lateinit var mTTS: TextToSpeech
    private lateinit var recipeVoiceFile: File
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: RecipeDetailsFragmentBinding
    private var fullRecipe: String = ""
    private val up: UserProfile = UserProfile()
    private lateinit var recipe : Recipe
    private lateinit var textTranslator: Translator
    private val espanol: Locale = Locale("es","es")
    //private lateinit var instructions: String
    private lateinit var translatedText: String
    private var flag: Int = -1

    //Enum class for supported languages
    enum class Language {
        English,
        Spanish,
        Japanese,
        Chinese,
        French
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
                //@TODO figure out what to do about recipe summaries
                //binding.recipeSummary.text = parsedRecipe.summary
                binding.recipeIngredients.text =
                    parsedRecipe.ingredients.joinToString("") { "- $it\n" }
                binding.recipeInstructions.text = parsedRecipe.steps.mapIndexed{index, s -> "${index+1}) $s" }.joinToString("") { "$it\n" }
                // No summary version
                fullRecipe = "Ingredients:... " + parsedRecipe.ingredients.toString() + "... " +
                        "Instructions:... " + parsedRecipe.steps.toString()
                translatedText = fullRecipe //grab text on create to be translated

                // Construct sound file
                recipeVoiceFile = File(
                    context?.cacheDir?.absolutePath,
                    tempView.selectedRecipe.value?.rId + ".wav"
                )
                // Always record recipe
                recordRecipe()
            }

            //Array adapter for spinner/drop down menu
            val aa = ArrayAdapter(requireActivity().applicationContext, R.layout.simple_spinner_item, Language.values())
            aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            val spinner = binding.langSpinner
            spinner.adapter = aa
            spinner.onItemSelectedListener = this

            binding.addFavorite.setOnClickListener {

                up.favoriteRecipes.add(recipe)
                UserProfileHelper.saveProfile(up)
            }

            // Setup Play button handler
            binding.playRecipeInstructionsButton.setOnClickListener {
                try {
                    if (recipeVoiceFile.exists()) {
                        mediaPlayer.start()
                        binding.pauseRecipeInstructionsButton.isEnabled = true
                    }
                } catch (e: Exception) {
                    Log.d("Recipe Details Fragment", "Error when playing audio")
                }
            }

            // Setup Replay button handler
            binding.replayRecipeInstructionsButton.setOnClickListener {
                    if (fullRecipe.isNotEmpty()) {
                        mTTS.speak(translatedText, TextToSpeech.QUEUE_ADD, null, UTTERANCE_ID)
                        binding.pauseRecipeInstructionsButton.isEnabled = true
                        Log.d("Recipe Details Fragment", "TTS successfully speak out recipe")
                    } else {
                        Log.e("Recipe Details Fragment", "No recipe instructions supplied for TTS")
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
        }

        return binding.root
    }

    /**
     * Spinner drop down listeners
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (Language.values()[position]) {
            Language.English -> {
                createTTS("English")
            }
            Language.Spanish -> {
                createTTS("Spanish")
            }
            Language.Japanese -> {
                createTTS("Japanese")
            }
            Language.Chinese -> {
                createTTS("Chinese")
            }
            Language.French -> {
                createTTS("French")
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    /**
     * Record translated recipe
     */
    private fun recordRecipe() {
        if (recipeVoiceFile.exists()) {
            recipeVoiceFile.delete()
        }
        val b = Bundle()
        if (translatedText.isNotEmpty()) {

            // Save cooking instructions as a sound file, this may take time
            mTTS.synthesizeToFile(translatedText, b, recipeVoiceFile, UTTERANCE_ID)
            mTTS.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d("Recipe Details Fragment", "Started synthesize To File")
                }

                override fun onDone(utteranceId: String?) {
                    if (utteranceId == UTTERANCE_ID) {
                        Log.d(
                            "Recipe Details Fragment",
                            "word is read, resuming with the next word"
                        )
                        if (recipeVoiceFile.exists()) {
                            mediaPlayer.reset() // Super important, need this to bind Media Player to new audio file
                            mediaPlayer.setDataSource(recipeVoiceFile.absolutePath)
                            mediaPlayer.prepareAsync()
                        }
                    }
                }

                override fun onError(utteranceId: String?) {
                    Log.e("Recipe Details Fragment", "Error synthesize to File")
                }
            })

            // Enable related buttons
            binding.playRecipeInstructionsButton.isEnabled = true
        }
    }
    /**
     * Creates TTS engine and translator model based on language
     */
    private fun createTTS(Lang: String){
        //Text to speech parameters
        data class TTSParams(val local: Locale, val lang: String)
        //Dictionary of currently supported languages
        val languageDiction = mapOf(
            "English" to TTSParams(Locale.UK, TranslateLanguage.ENGLISH),
            "Spanish" to TTSParams(espanol, TranslateLanguage.SPANISH),
            "Japanese" to TTSParams(Locale.JAPANESE, TranslateLanguage.JAPANESE),
            "Chinese" to TTSParams(Locale.CHINESE, TranslateLanguage.CHINESE),
            "French" to TTSParams(Locale.FRENCH, TranslateLanguage.FRENCH))
        //Create text to speech engine
        mTTS = TextToSpeech(
            activity?.applicationContext
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                //if there is no error then set language based on language
                val textToSpeechParams = languageDiction[Lang]
                if (textToSpeechParams != null) {
                    mTTS.language = textToSpeechParams.local //set language of engine
                }
                /**
                 * This flag is to prevent asynch issues
                 * Since English is default, this prevents translating English -> English
                 * Translate back to English if re-selected
                 */
                if(Lang == "English"){
                    flag += 1
                    if(flag > 0) {
                        translatedText = fullRecipe
                    }
                }
                /**
                 * Set up translator for languages
                 * not English
                 */
                else {
                    //Set up translator options
                    val options = textToSpeechParams?.let {
                        TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.ENGLISH)
                            .setTargetLanguage(it.lang)
                            .build()
                    }
                    //Create language model
                    textTranslator = options?.let { Translation.getClient(it) }!!
                    var conditions = DownloadConditions.Builder()
                        .requireWifi()
                        .build()
                    //download language model
                    //Language models are around 30MB, don't have too many.
                    textTranslator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener {
                            // Model downloaded successfully. Okay to start translating
                            // Set a flag, unhide the translation UI, etc.
                            Log.d("Translator", "Model download successful")
                            textTranslator.translate(fullRecipe)
                                .addOnSuccessListener {
                                    Log.d("Translated text", it) //it refers to the translated string
                                    translatedText = it

                                    // Record translated recipe
                                    Log.d("Translated tex", "Record translated text")
                                    recordRecipe()
                                }
                        }
                        .addOnFailureListener {
                            //Model couldn't be downloaded or other internal error
                            Log.e("Translator", "Model download failed")
                        }
                }
            }
        }
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