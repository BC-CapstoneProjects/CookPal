package bellevuecollege.edu.cookpal.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.ListFavoriteRecipeBinding
import bellevuecollege.edu.cookpal.network.Recipe
import android.graphics.drawable.Drawable
import java.io.InputStream
import java.lang.Exception
import java.net.URL


class FavoriteRecipeAdapter(private val context: Context, private val favoriteRecipes: ArrayList<Recipe>) : BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private lateinit var binding: ListFavoriteRecipeBinding

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

        // Get title element
        //val titleTextView = binding.recipeListTitle

        // Get thumbnail element
        val thumbnailImageView = binding.recipeListThumbnail

        //val recipe = getItem(position) as Recipe
        //titleTextView.text = recipe.title

        //thumbnailImageView.setImageDrawable(loadImageFromWebOperations(recipe.imageUrl))

        return inflater.inflate(R.layout.fragment_favorite_recipes, parent, false)
    }

    private fun loadImageFromWebOperations(url: String?): Drawable? {
        return try {
            val `is`: InputStream = URL(url).content as InputStream
            Drawable.createFromStream(`is`, "src name")
        } catch (e: Exception) {
            null
        }
    }
}