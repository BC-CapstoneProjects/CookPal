package bellevuecollege.edu.cookpal.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bellevuecollege.edu.cookpal.R
import bellevuecollege.edu.cookpal.databinding.GridRowRecipeBinding
import bellevuecollege.edu.cookpal.network.Recipe

class RecipeGridAdapter(private val onClickListener: OnClickListener) : ListAdapter<Recipe,
        RecipeGridAdapter.IngredientSearchRecipeViewHolder>(DiffCallback){

    class IngredientSearchRecipeViewHolder(private var binding: GridRowRecipeBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(ingredientRecipe: Recipe) {
            // Recipe image
            binding.recipe = ingredientRecipe
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
            return oldItem.rId == newItem.rId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): IngredientSearchRecipeViewHolder {
        return IngredientSearchRecipeViewHolder(GridRowRecipeBinding.inflate(LayoutInflater.from(parent.context)))
    }

    /* Knows what position in recipe grid user clicked on */
    override fun onBindViewHolder(holder: IngredientSearchRecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.itemView.setOnClickListener { //view: View ->
            //view.findNavController().navigate(R.id.action_recipeResultsFragment_to_recipeDetailsFragment)
            onClickListener.onClick(recipe)

        }
        holder.bind(recipe)
    }

    class OnClickListener(val clickListener: (recipe:Recipe) -> Unit) {
        fun onClick(recipe: Recipe) = clickListener(recipe)
    }
}