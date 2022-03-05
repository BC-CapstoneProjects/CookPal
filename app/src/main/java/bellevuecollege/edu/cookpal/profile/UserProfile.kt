package bellevuecollege.edu.cookpal.profile

import com.example.testrecipeapi.Recipe

class UserProfile {

    constructor() {

    }

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

        try {
            profilePhotoPath = mp.get("profilePhotoPath") as String
        } catch (e: Exception) {

        }
    }

    var name: String = ""
    var emailAddress: String = ""
    var profilePhotoPath: String = ""
    val favoriteRecipes: ArrayList<Recipe> = ArrayList<Recipe>()
}