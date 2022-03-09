package bellevuecollege.edu.cookpal.network

import android.content.ContentValues
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DownloadRecipesFirebase {

    fun getRecipes(keyWord: String, myCallback: (result: List<Recipe>) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("/recipes/").orderByValue()


        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val downloadedRecipes: ArrayList<Recipe> = arrayListOf()
                dataSnapshot.children.forEach {
                    downloadedRecipes.add(it.getValue(Recipe().javaClass)!!)
                    Log.d(ContentValues.TAG, downloadedRecipes[downloadedRecipes.size-1].title)
                    Log.d(ContentValues.TAG, downloadedRecipes[downloadedRecipes.size-1].steps.toString())
                }
                myCallback.invoke(downloadedRecipes.filter{it.title.contains(keyWord, true)})
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }

}