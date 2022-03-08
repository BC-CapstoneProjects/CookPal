package bellevuecollege.edu.cookpal.network

import android.content.ContentValues
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DownloadRecipesFirebase {
    val downloadedRecipes: ArrayList<RecipeData> = arrayListOf()
    fun getRecipes(myCallback: (result: ArrayList<RecipeData>) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("/recipes/")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (child in dataSnapshot.children) {
                    val childData = child.value as Map<*, *>
                    val recipe = RecipeData()
                    recipe.imgUrl = childData["imgUrl"] as String
                    recipe.ingredients = childData["ingredients"] as ArrayList<String>
                    recipe.rating = childData["rating"] as String
                    recipe.reviewNumber = childData["reviewNumber"] as Number
                    recipe.title = childData["title"] as String
                    recipe.sourceUrl = childData["sourceUrl"] as String
                    recipe.totalTime = childData["totalTime"] as String
                    downloadedRecipes.add(recipe)
                }
                myCallback.invoke(downloadedRecipes)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })

    }
}