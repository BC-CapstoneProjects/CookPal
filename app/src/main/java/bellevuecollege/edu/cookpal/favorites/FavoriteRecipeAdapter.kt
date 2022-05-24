package bellevuecollege.edu.cookpal.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.FragmentFavoriteRecipesBinding
import bellevuecollege.edu.cookpal.recipes.Recipe

class FavoriteRecipeAdapter(private val context: Context, private val favoriteRecipes: ArrayList<Recipe>) : BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var binding: FragmentFavoriteRecipesBinding

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
        return inflater.inflate(R.layout.fragment_favorite_recipes, parent, false)
    }

}