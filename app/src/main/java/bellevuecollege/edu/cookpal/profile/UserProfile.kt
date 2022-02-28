package bellevuecollege.edu.cookpal.profile

import com.example.testrecipeapi.Recipe
import java.lang.Exception

class UserProfile {

    constructor(){

    }

    constructor(mp :Map<String, String>?) {
        setProfile(mp)
    }

    fun setProfile(mp :Map<String, String>?) {
        if (mp == null) {
            return;
        }

        try {
            val favoriterecipes = mp.get("favoriterecipes") as ArrayList<Recipe>

            for (value in favoriterecipes) {
                favoriteRecipes.add(value)
            }
        } catch (e: Exception) {

        }

        try {
            emailAddress = mp.get("emailAddress") as String
        } catch (e: Exception) {

        }

        try {
            name = mp.get("name") as String
        } catch (e: Exception) {

        }
    }
    var name : String = "test"
    var emailAddress : String = "testem"
    val favoriteRecipes : ArrayList<Recipe> = ArrayList<Recipe>()
}