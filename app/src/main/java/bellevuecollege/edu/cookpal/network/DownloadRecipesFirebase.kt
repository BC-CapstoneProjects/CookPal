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
                val downloadedRecipes =
                    dataSnapshot.children.map { it.getValue(Recipe().javaClass)!! }
                myCallback.invoke(downloadedRecipes.filter { it.title.contains(keyWord, true) })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }

}