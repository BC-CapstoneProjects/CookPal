package bellevuecollege.edu.cookpal.recipes

import android.app.Activity
import android.app.Application
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.RecipeResultsFragmentBinding
import bellevuecollege.edu.cookpal.network.DownloadRecipesMongoDB
import bellevuecollege.edu.cookpal.network.Recipe
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.socket.client.Socket
import kotlinx.coroutines.launch
import org.json.JSONObject



enum class IngredientSearchApiStatus { LOADING, ERROR, DONE }

class RecipeResultsViewModel(application: Application) : AndroidViewModel(application) {
    private var ALL_RECIPES_NAME: String = "allrecipes"
    private var _searchTerm: String = "banh mi"
    private val context = getApplication<Application>().applicationContext
    private lateinit var bd:RecipeResultsFragmentBinding
    private var showingFilterPopupMenu:Boolean = false
    private lateinit var popupWindow:PopupWindow
    private var view: View? = null

    data class Response(val document: Recipe)

    // The internal MutableLiveData String that stores the most recent response
    private val _status = MutableLiveData<IngredientSearchApiStatus>()

    // The external immutable LiveData for the response String
    val status: LiveData<IngredientSearchApiStatus>
        get() = _status

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>>
        get() = _recipes

    private val _navigateToSelectedRecipe = MutableLiveData<Recipe?>()
    val navigateToSelectedRecipe: MutableLiveData<Recipe?>
        get() = _navigateToSelectedRecipe

    private val _searchButtonVisible = MutableLiveData<Boolean?>()
    val searchButtonVisible: LiveData<Boolean?>
        get() = _searchButtonVisible

    fun setBinding(bd1:RecipeResultsFragmentBinding){
        bd = bd1
    }

    fun setSearchTerm(searchKeyWord: String) {
        _searchTerm = searchKeyWord
        _searchButtonVisible.value = searchKeyWord.isNotEmpty()
    }

    fun Context.hideKeyboard(pview: View) {

        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(pview.windowToken, 0)
    }

    private lateinit var sk:Socket
    private lateinit var uuid:String
    private var recs:ArrayList<Recipe> = ArrayList()

    fun setSocketObject(psk: Socket, puuid:String) {
        sk = psk
        uuid = puuid

        sk?.on("senddata") { paramters->
            println("mSocket func")
            var ob = paramters[0] as JSONObject

            println(ob.toString())

            val typeRef = object : com.fasterxml.jackson.core.type.TypeReference<bellevuecollege.edu.cookpal.network.Recipe>() {}

            var rep:Recipe = jacksonObjectMapper().readValue(ob.toString(), typeRef)

            for (rec in _recipes.value!!) {
                // if item from webscraper result is already in list we are done no need to add it to the list
                if (rec.id == rep.id && rep.id != "") {
                    return@on
                }
            }

            recs.add(rep)

            // is needed because otherwise the list doesn't update on the view, all objects need to be new ones
            var recs2:ArrayList<Recipe> = ArrayList()

            for (item in recs)
            {
                recs2.add(item.copy())
            }

            viewModelScope.launch {

               try
               {
                   _recipes.value = recs2
               }
               catch (e:Exception)
               {

               }
            }
        }
    }

    /**
     * Call getIngredientSearchRecipes() on init so we can display status immediately.
     */
    init {
        getIngredientSearchRecipes()
    }
    val MIN_MINUTES:Int = 5

    var minMins:Int? = MIN_MINUTES
    var maxMins:Int? = MIN_MINUTES
    var rating:Float? = 0.0f
    var ingredients:String? = ""

    var filter: RecipeFilter? = null;

    fun openFilter() {

        // inflate the layout of the popup window
        try
        {

            val inflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popupView: View? = inflater?.inflate(R.layout.fragment_filter, null)

            popupView?.setBackgroundColor(0xFFe6eaed.toInt());

        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it

        popupWindow = PopupWindow(popupView, width, height, focusable)
            var ingStr = ""

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(bd.root, Gravity.TOP, 0, 350)

            var sbMinMins:SeekBar? =  popupView?.findViewById<SeekBar>(R.id.sbMinMinsFilter)
            var sbMaxMins:SeekBar? =  popupView?.findViewById<SeekBar>(R.id.sbMaxMinsFilter)
            var sbRating:SeekBar? =  popupView?.findViewById<SeekBar>(R.id.sbRatingFilter)
            var txtMinMins:TextView? =  popupView?.findViewById<TextView>(R.id.lblMinMins)
            var txtMaxMins:TextView? =  popupView?.findViewById<TextView>(R.id.lblMaxMins)
            var txtRating:TextView? =  popupView?.findViewById<TextView>(R.id.lblRating)
            var ing:EditText? =  popupView?.findViewById<EditText>(R.id.txtFilterIngredients)

            ing?.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    ingredients = s.toString()
                }

                override fun beforeTextChanged(
                        s: CharSequence, start: Int,
                        count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                        s: CharSequence, start: Int,
                        before: Int, count: Int
                ) {
                }
            })

            var btnapply:Button? =  popupView?.findViewById<Button>(R.id.buttonApplyFilter)
            var btncancel:Button? =  popupView?.findViewById<Button>(R.id.buttonCancelFilter)
            var btnremove:Button? =  popupView?.findViewById<Button>(R.id.buttonRemoveFilter)

            ingStr = ing?.text.toString()

            if (filter != null) {
                ing?.setText(filter?.ingredients)
                sbMinMins?.progress = filter?.minMins!! - MIN_MINUTES
                sbRating?.progress = (filter?.rating?.times(2))?.toInt()!!
                sbMaxMins?.progress = filter?.maxMins!! - MIN_MINUTES
            }

            minMins = sbMinMins?.progress?.plus(MIN_MINUTES)
            rating = sbRating?.progress?.toFloat()
            rating = rating?.div(2.0f)

            maxMins = sbMaxMins?.progress?.plus(MIN_MINUTES)
            ingredients = ing?.text.toString()

            txtRating?.text = rating.toString()
            txtMaxMins?.text = maxMins.toString()
            txtMinMins?.text = minMins.toString()

            btnapply?.setOnClickListener {
                popupWindow.dismiss()
                filter = RecipeFilter()
                filter?.maxMins = maxMins
                filter?.minMins = minMins
                filter?.rating = rating
                filter?.ingredients = ingredients
                getIngredientSearchRecipes()
            }

            btncancel?.setOnClickListener {
                popupWindow.dismiss()
            }

            btnremove?.setOnClickListener {
                popupWindow.dismiss()
                filter = null
                txtRating?.text = ""
                sbMinMins?.progress = 0
                sbRating?.progress = 0
                sbMaxMins?.progress = 0
                maxMins = MIN_MINUTES
                minMins = MIN_MINUTES
                rating = 0.0f
                ingredients = ""

                getIngredientSearchRecipes()
            }

            sbRating?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    rating = progress / 2.0f
                    txtRating?.text = rating.toString()
                    // println("progressChangedValue: " + progressChangedValue.toString())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // TODO Auto-generated method stub
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })


            sbMaxMins?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    maxMins = progress + MIN_MINUTES

                    if (maxMins!! < minMins!!)
                    {
                        minMins = maxMins

                        txtMinMins?.text = minMins.toString()

                        sbMinMins?.progress = minMins!! - MIN_MINUTES
                    }

                    txtMaxMins?.text = maxMins.toString()
                    // println("progressChangedValue: " + progressChangedValue.toString())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // TODO Auto-generated method stub
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })

            sbMinMins?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    minMins = progress + MIN_MINUTES

                    if (maxMins!! < minMins!!)
                    {
                        maxMins = minMins

                        txtMaxMins?.text = maxMins.toString()

                        sbMaxMins?.progress = maxMins!! - MIN_MINUTES
                    }

                    txtMinMins?.text = minMins.toString()
                   // println("progressChangedValue: " + progressChangedValue.toString())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // TODO Auto-generated method stub
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })
        }
        catch (e:Exception)
        {
            System.out.println(e.message)
        }
    }
    
    fun setView(pview:View) {
        view = pview
    }
    /**
     * Sets the value of the status LiveData to the IngredientSearch API status.
     */
    fun getIngredientSearchRecipes() {
        if (view != null) {
            context.hideKeyboard(view!!)
            bd.searchBox.clearFocus()
        }

        viewModelScope.launch {
            Log.d("RecipeResultsViewModel", "Retrieving recipes for $_searchTerm")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                    DownloadRecipesMongoDB().getRecipesByTitle(
                        _searchTerm,context,uuid,filter
                    )
                Log.d("RecipeResultsViewModel", "Successfully get recipes")

                _recipes.value = searchResponse
                recs = searchResponse as ArrayList<Recipe>
                _status.value = IngredientSearchApiStatus.DONE
            } catch (e: Exception) {
                _status.value = IngredientSearchApiStatus.ERROR
                _recipes.value = ArrayList()
                recs = ArrayList()
            }
        }
    }

    fun displayRecipeDetails(recipe: Recipe) {
        _navigateToSelectedRecipe.value = recipe
    }

    fun displayRecipeDetailsComplete() {
        _navigateToSelectedRecipe.value = null
    }
}