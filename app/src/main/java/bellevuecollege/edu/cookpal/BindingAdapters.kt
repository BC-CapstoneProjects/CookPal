package bellevuecollege.edu.cookpal

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import bellevuecollege.edu.cookpal.network.Recipe
import bellevuecollege.edu.cookpal.recipes.IngredientSearchApiStatus
import bellevuecollege.edu.cookpal.recipes.RecipeGridAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

@BindingAdapter("recipeImage")
fun bindRecipeImage(imgView: ImageView, recipe: Recipe?) {
    recipe?.let {
        val imgUri = recipe.imgSrcUrl.toUri().buildUpon().scheme("https").build()
        Log.d("recipeImage", imgUri.toString())
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    recipe.isLoadedSuccessful = false
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    recipe.isLoadedSuccessful = true
                    return false
                }
            })
            .into(imgView)
    }
}

@BindingAdapter("listData")
fun bindRecyclerView(
    recyclerView: RecyclerView,
    data: List<Recipe>?
) {
    val adapter = recyclerView.adapter as RecipeGridAdapter
    adapter.submitList(data)
}

@BindingAdapter("ingredientSearchApiStatus")
fun bindStatus(
    statusImageView: ImageView,
    status: IngredientSearchApiStatus
) {
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

@BindingAdapter("viewRecipeInstructions")
fun bindRecipeInstructions(webView: WebView, recipe: Recipe?) {
    recipe?.let {
        webView.loadUrl(recipe.sourceUrl)
    }
}

