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

        // Get title element
        val titleTextView = rowView.findViewById(R.id.recipe_list_title) as TextView

        // Get thumbnail element
        val thumbnailImageView = rowView.findViewById(R.id.recipe_list_thumbnail) as ImageView

        //Get recipe information
        var map : HashMap<String, String> = getItem(position) as HashMap<String, String>

        titleTextView.text = map["title"].toString()
        val recipeImageURL = map["imageUrl"].toString()

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