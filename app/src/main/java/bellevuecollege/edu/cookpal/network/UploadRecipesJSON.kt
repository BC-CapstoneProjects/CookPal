package bellevuecollege.edu.cookpal.network

import android.content.Context
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.firebase.database.FirebaseDatabase

class UploadRecipesJSON {
    fun uploadRecipes(fileName: String, context: Context?) {
        val file = context?.assets?.open("recipes/$fileName")
        val database = FirebaseDatabase.getInstance().getReference("/recipes/")
        val typeRef = object : TypeReference<List<RecipeData>>() {}
        val recipes = jacksonObjectMapper().readValue(file, typeRef)
        recipes.forEach { database.push().setValue(it) }
    }
}