package bellevuecollege.edu.cookpal.recipes

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
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
import kotlinx.coroutines.launch


enum class IngredientSearchApiStatus { LOADING, ERROR, DONE }

class RecipeResultsViewModel(application: Application) : AndroidViewModel(application) {
    private var ALL_RECIPES_NAME: String = "allrecipes"
    private var _searchTerm: String = "banh mi"
    private val context = getApplication<Application>().applicationContext
    private lateinit var bd:RecipeResultsFragmentBinding
    private var showingFilterPopupMenu:Boolean = false
    private lateinit var popupWindow:PopupWindow

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

    /**
     * Call getIngredientSearchRecipes() on init so we can display status immediately.
     */
    init {
        getIngredientSearchRecipes()
    }

    fun openFilter() {

        // inflate the layout of the popup window
        try
        {

            val inflater:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val popupView: View? = inflater?.inflate(R.layout.fragment_filter, null)

        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it

        popupWindow = PopupWindow(popupView, width, height, focusable)
            var ingStr = ""

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(bd.root, Gravity.TOP, 0, 350)

            var minMins = 10
            var maxMins = 10
            var rating:Float = 0.0f

            var sb:SeekBar? =  popupView?.findViewById<SeekBar>(R.id.seekBar)
            var sbm:SeekBar? =  popupView?.findViewById<SeekBar>(R.id.seekBarMax)
            var sbr:SeekBar? =  popupView?.findViewById<SeekBar>(R.id.seekBarRate)
            var tx:TextView? =  popupView?.findViewById<TextView>(R.id.textViewProg)
            var txm:TextView? =  popupView?.findViewById<TextView>(R.id.textViewProgMax)
            var txr:TextView? =  popupView?.findViewById<TextView>(R.id.textViewProgRate)
            var ing:EditText? =  popupView?.findViewById<EditText>(R.id.filterIngredients)

            var btnapply:Button? =  popupView?.findViewById<Button>(R.id.buttonApplyFilter)
            var btncancel:Button? =  popupView?.findViewById<Button>(R.id.buttonCancelFilter)
            var btnremove:Button? =  popupView?.findViewById<Button>(R.id.buttonRemoveFilter)

            ingStr = ing?.text.toString()

            btnapply?.setOnClickListener {
                popupWindow.dismiss()
                showingFilterPopupMenu = false
            }

            btncancel?.setOnClickListener {
                popupWindow.dismiss()
                showingFilterPopupMenu = false
            }

            btnremove?.setOnClickListener {
                popupWindow.dismiss()
                showingFilterPopupMenu = false
            }

            sbr?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    rating = progress / 2.0f
                    txr?.text = rating.toString()
                    // println("progressChangedValue: " + progressChangedValue.toString())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // TODO Auto-generated method stub
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })


            sbm?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    maxMins = progress + 10

                    if (maxMins < minMins)
                    {
                        minMins = maxMins

                        tx?.text = minMins.toString()

                        sb?.progress = minMins - 10
                    }

                    txm?.text = maxMins.toString()
                    // println("progressChangedValue: " + progressChangedValue.toString())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    // TODO Auto-generated method stub
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                }
            })

            sb?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    minMins = progress + 10

                    if (maxMins < minMins)
                    {
                        maxMins = minMins

                        txm?.text = maxMins.toString()

                        sbm?.progress = maxMins - 10
                    }

                    tx?.text = minMins.toString()
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

    /**
     * Sets the value of the status LiveData to the IngredientSearch API status.
     */
    fun getIngredientSearchRecipes() {
        viewModelScope.launch {
            Log.d("RecipeResultsViewModel", "Retrieving recipes for $_searchTerm")
            _status.value = IngredientSearchApiStatus.LOADING
            try {
                val searchResponse =
                    DownloadRecipesMongoDB().getRecipes(

                        _searchTerm,context

                    )
                Log.d("RecipeResultsViewModel", "Successfully get recipes")

                _recipes.value = searchResponse
                _status.value = IngredientSearchApiStatus.DONE
            } catch (e: Exception) {
                _status.value = IngredientSearchApiStatus.ERROR
                _recipes.value = ArrayList()
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