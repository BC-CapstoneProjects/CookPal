package bellevuecollege.edu.cookpal.profile

import android.util.Log
import bellevuecollege.edu.cookpal.network.Recipe

class UserProfile {

    constructor()

    constructor(email: String) {
        emailAddress = email
    }

    constructor(mp: Map<String, String>?) {
        setProfile(mp)
    }

    fun setProfile(mp: Map<String, String>?) {
        if (mp == null) {
            return;
        }

        try {
            favoriteRecipes = mp["favoriteRecipes"] as ArrayList<Recipe>
        } catch (e: Exception) {

        }

        try {
            emailAddress = mp["emailAddress"] as String
        } catch (e: Exception) {

        }

        try {
            name = mp["name"] as String
        } catch (e: Exception) {

        }

        try {
            profilePhotoPath = mp["profilePhotoPath"] as String
        } catch (e: Exception) {

        }
    }
    var name : String = ""
    var emailAddress : String = ""
    var profilePhotoPath : String = ""
    var favoriteRecipes : ArrayList<Recipe> = ArrayList()
    var tempFR : ArrayList<Recipe> = ArrayList()
}