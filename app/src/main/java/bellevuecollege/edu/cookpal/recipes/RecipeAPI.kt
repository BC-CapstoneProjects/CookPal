package bellevuecollege.edu.cookpal.recipes

import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class RecipeAPI {
    companion object {
        @RequiresApi(Build.VERSION_CODES.N)
        fun search(searchval: String): ArrayList<Recipe> {

            var flist = ArrayList<Recipe>()
            try {
                var paramSearch = searchval.replace(" ", "%20")

                var urlm =
                    "https://api.edamam.com/api/recipes/v2?type=public&q=" + paramSearch + "&app_id=9e739484&app_key=e3b4d6f7a98479690a4e75d907cd721c%09"
                val url = URL(urlm)
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "GET"

                    println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

                    inputStream.bufferedReader().use {
                        it.lines().forEach { line ->
                            println(line)

                            val data = JSONObject(line)


                            val ary = (data["hits"] as JSONArray)


                            for (i in 0 until ary.length()) {
                                val item = ary.getJSONObject(i)

                                var fitem = Recipe()
                                val rec = item["recipe"] as JSONObject
                                fitem.image = rec.get("image") as String
                                fitem.label = rec.get("label") as String
                                fitem.url = rec.get("url") as String
                                fitem.totalTime = rec.get("totalTime") as Double

                                var jsAry = rec.get("cuisineType") as JSONArray

                                for (i in 0 until jsAry.length()) {
                                    val str = jsAry.getString(i)

                                    fitem.cuisineType.add(str)
                                }

                                jsAry = rec.get("mealType") as JSONArray

                                for (i in 0 until jsAry.length()) {
                                    val str = jsAry.getString(i)

                                    fitem.mealType.add(str)
                                }

                                jsAry = rec.get("dishType") as JSONArray

                                for (i in 0 until jsAry.length()) {
                                    val str = jsAry.getString(i)

                                    fitem.dishType.add(str)
                                }

                                jsAry = rec.get("ingredients") as JSONArray

                                for (i in 0 until jsAry.length()) {
                                    val ingredient = jsAry.getJSONObject(i)
                                    val ing = Ingredient()

                                    ing.text = ingredient.getString("text")
                                    ing.food = ingredient.getString("food")
                                    ing.foodCategory = ingredient.getString("foodCategory")
                                    ing.measure = ingredient.getString("measure")
                                    ing.quantity = ingredient.getDouble("quantity")
                                    ing.weight = ingredient.getDouble("weight")

                                    fitem.ingredients.add(ing)
                                }

                                flist.add(fitem)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }

            return flist;
        }
    }
}

