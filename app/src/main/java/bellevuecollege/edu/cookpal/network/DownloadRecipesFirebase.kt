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
                dataSnapshot.children.map { downloadedRecipes.add(it.getValue(RecipeData().javaClass)!!) }
                myCallback.invoke(downloadedRecipes)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })

    }
}