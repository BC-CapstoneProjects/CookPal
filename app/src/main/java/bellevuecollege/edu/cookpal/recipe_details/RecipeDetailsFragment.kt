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

    /**
     * Can remove onCreate function
     * spinner is default to english so
     * TTS engine is created there.
     * See OnItemSelected function down below.
     */
    //override fun onCreate(savedInstanceState: Bundle?) {
    //super.onCreate(savedInstanceState)
    // Setup Text To Speech engine
    // Defaults to english
//        mTTS = TextToSpeech(
//            activity?.applicationContext
//        ) { status ->
//            if (status != TextToSpeech.ERROR) {
//                //if there is no error then set language
//                mTTS.language = Locale.UK
//            }
//        }
    //}

    //Enum class for supported language
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

                //@TODO Old instructions code, may need to remove
                //instructions = recipe.steps.mapIndexed{index, s -> "${index+1}) $s" }.joinToString("") { "$it\n" }

                // Construct full recipe
//                fullRecipe = "Summary:... " + parsedRecipe.summary + "... " +
//                        "Ingredients:... " + parsedRecipe.ingredients.toString() + "... " +
//                        "Instructions:... " + parsedRecipe.steps.toString()
                // No summary version
                fullRecipe = "Ingredients:... " + parsedRecipe.ingredients.toString() + "... " +
                        "Instructions:... " + parsedRecipe.steps.toString()
                translatedText = fullRecipe //grab text on create to be translated
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

            // Setup Speak button handler
            binding.speakRecipeInstructionsButton.setOnClickListener {
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

    /**
     * Spinner drop down listeners
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (Language.values()[position]) {
            Language.English -> {
                //English set up
                mTTS = TextToSpeech(
                    activity?.applicationContext
                ) { status ->
                    if (status != TextToSpeech.ERROR) {
                        //if there is no error then set language
                        mTTS.language = Locale.UK
                    }
                }
                flag+=1
                if(flag > 0) {
                    translatedText = fullRecipe
                }
            }
            Language.Spanish -> {
                //Set up text to speech language
                mTTS = TextToSpeech(
                    activity?.applicationContext
                ) { status ->
                    if (status != TextToSpeech.ERROR) {
                        //if there is no error then set language
                        mTTS.language = espanol
                    }
                }

                /**
                 * Set up spanish translator model
                 */
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.SPANISH)
                    .build()
                textTranslator = Translation.getClient(options)
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
                            }
                    }
                    .addOnFailureListener {
                        //Model couldn't be downloaded or other internal error
                        Log.e("Translator", "Model download failed")
                    }
            }
            Language.Japanese -> {
                //Set up TTS language
                mTTS = TextToSpeech(
                    activity?.applicationContext
                ) { status ->
                    if (status != TextToSpeech.ERROR) {
                        //if there is no error then set language
                        mTTS.language = Locale.JAPANESE
                    }
                }

                /**
                 * Set up japanese translator model
                 */
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.JAPANESE)
                    .build()
                textTranslator = Translation.getClient(options)
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
                            }
                    }
                    .addOnFailureListener {
                        //Model couldn't be downloaded or other internal error
                        Log.e("Translator", "Model download failed")
                    }
            }

            Language.Chinese -> {
                //Set up TTS language
                mTTS = TextToSpeech(
                    activity?.applicationContext
                ) { status ->
                    if (status != TextToSpeech.ERROR) {
                        //if there is no error then set language
                        mTTS.language = Locale.CHINESE
                    }
                }

                /**
                 * Set up chinese translator model
                 */
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.CHINESE)
                    .build()
                textTranslator = Translation.getClient(options)
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
                            }
                    }
                    .addOnFailureListener {
                        //Model couldn't be downloaded or other internal error
                        Log.e("Translator", "Model download failed")
                    }
            }

            Language.French -> {
                //Set up TTS language
                mTTS = TextToSpeech(
                    activity?.applicationContext
                ) { status ->
                    if (status != TextToSpeech.ERROR) {
                        //if there is no error then set language
                        mTTS.language = Locale.FRENCH
                    }
                }

                /**
                 * Set up french translator model
                 */
                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.FRENCH)
                    .build()
                textTranslator = Translation.getClient(options)
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
                            }
                    }
                    .addOnFailureListener {
                        //Model couldn't be downloaded or other internal error
                        Log.e("Translator", "Model download failed")
                    }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    fun createTTSEngine(){

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