package bellevuecollege.edu.cookpal

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.recipes.IngredientSearchApiStatus
import bellevuecollege.edu.cookpal.recipes.RecipeGridAdapter


@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView,
                     data: List<Recipe>?) {
    val adapter = recyclerView.adapter as RecipeGridAdapter
    adapter.submitList(data)
}

@BindingAdapter("ingredientSearchApiStatus")
fun bindStatus(statusImageView: ImageView,
status: IngredientSearchApiStatus) {
    when (status) {
        IngredientSearchApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        IngredientSearchApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        IngredientSearchApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}
