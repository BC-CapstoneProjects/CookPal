package bellevuecollege.edu.cookpal.profile

import com.example.testrecipeapi.Recipe
import java.lang.Exception

class UserProfile {

    constructor(){

    }

    constructor(mp :Map<String, String>?) {
        setProfile(mp)
    }

    fun setProfile(mp :Map<String, String>?)
    {
        if (mp == null)
        {
            return;
        }

        try {
            val favoriterecipes = mp.get("favoriterecipes") as ArrayList<Recipe>

            for ( value in favoriterecipes) {
                favoriteRecipes.add(value)
            }
        }
        catch (e : Exception)
        {

        }
    }

    val favoriteRecipes : ArrayList<Recipe> = ArrayList<Recipe>()
}