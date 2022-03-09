package bellevuecollege.edu.cookpal.network

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.firebase.database.FirebaseDatabase

class UploadRecipesJSON {
    fun uploadAllRecipeFiles(context: Context?) {
        val assetManager = context?.assets!!
        val files = assetManager.list("recipes")
        val database = FirebaseDatabase.getInstance().getReference("/recipes/")
        val typeRef = object : TypeReference<List<Recipe>>() {}
        Log.d(ContentValues.TAG, "We have ${files?.size}")
        files!!.forEach {
            Log.d(ContentValues.TAG, it.toString())
            val recipes =
                jacksonObjectMapper().readValue(context?.assets?.open("recipes/$it"), typeRef)
            recipes.forEach { recipe -> database.push().setValue(recipe) }
        }
    }

    fun uploadRecipeFile(fileName: String, context: Context?) {

        val file = context?.assets?.open("recipes/$fileName")
        val database = FirebaseDatabase.getInstance().getReference("/recipes/")
        val typeRef = object : TypeReference<List<Recipe>>() {}
        val recipes = jacksonObjectMapper().readValue(file, typeRef)
        recipes.forEach { recipe -> database.push().setValue(recipe) }


    }
}