package bellevuecollege.edu.cookpal.profile

import bellevuecollege.edu.cookpal.recipes.Recipe

class UserProfile {

    constructor(){

    }

    constructor(email:String){
        emailAddress = email
    }

    constructor(mp :Map<String, String>?) {
        setProfile(mp)
    }

    fun setProfile(mp :Map<String, String>?) {
        if (mp == null) {
            return;
        }

        try {
            val favoriteRecipes = mp["favoriteRecipes"] as ArrayList<Recipe>

            for (value in favoriteRecipes) {
                this.favoriteRecipes.add(value)
            }
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
            profilePhotoPath = mp.get("profilePhotoPath") as String
        } catch (e: Exception) {

        }
    }
    var name : String = ""
    var emailAddress : String = ""
    var profilePhotoPath : String = ""
    private val favoriteRecipes : ArrayList<Recipe> = ArrayList<Recipe>()
}