package bellevuecollege.edu.cookpal.favorites

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.network.Recipe
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class FavoriteRecipeAdapter(private val context: Context, private val favoriteRecipes: ArrayList<Recipe>) : BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return favoriteRecipes.size
    }

    override fun getItem(position: Int): Any {
        return favoriteRecipes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val rowView = inflater.inflate(R.layout.list_favorite_recipe, parent, false)

        //val favoriteRecipeHashMap : HashMap<Int, Recipe> = HashMap()

        // Get title element
        val titleTextView = rowView.findViewById(R.id.recipe_list_title) as TextView

        // Get thumbnail element
        val thumbnailImageView = rowView.findViewById(R.id.recipe_list_thumbnail) as ImageView

        titleTextView.text = "Nice"
        val testUrl = "https://food.fnr.sndimg.com/content/dam/images/food/fullset/2017/1/6/1/KC1201_Cauliflower-Fried-Rice_s4x3.jpg.rend.hgtvcom.826.620.suffix/1529948516413.jpeg"

        //@TODO java.lang.ClassCastException: java.util.HashMap cannot be cast to bellevuecollege.edu.cookpal.network.Recipe
        val recipe = getItem (position)
        val recipeAsString = recipe.toString()
        val recipeImageURL = recipeAsString.substringAfter("imageUrl=").substringBefore(", loadedSuccessful")
        val recipeTitle = recipeAsString.substringAfter("title=").substringBefore(", steps")
        Log.d("Favorite Recipe adapter 1", recipeImageURL)
        Log.d("Favorite Recipe adapter 2", recipeTitle)

        //val recipe = getItem(position) as Recipe
        titleTextView.text = recipeTitle

        //Coroutine to load image from web
        //Cannot load images using main thread
        GlobalScope.launch {
            loadImageFromWeb(recipeImageURL, thumbnailImageView)
        }

        return rowView
    }

    //Load image from web
    private suspend fun loadImageFromWeb(url: String?, thumbnail: ImageView) {
        withContext(Dispatchers.IO) {
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(url).openStream()
                image = BitmapFactory.decodeStream(`in`)
                thumbnail.setImageBitmap(image)
            } catch (e: Exception) {
                Log.d("Favorite recipe adapter error", e.toString())
            }
        }
    }
}