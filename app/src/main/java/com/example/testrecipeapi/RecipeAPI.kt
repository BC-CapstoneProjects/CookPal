package com.example.testrecipeapi

import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class RecipeAPI {
    companion object {
        @RequiresApi(Build.VERSION_CODES.N)
        fun search(searchval : String): MutableList<String> {

            var list = mutableListOf("")

            try {

                var parmSearch = searchval

                parmSearch.replace(" ", "%20")

                var urlm = "https://api.edamam.com/api/recipes/v2?type=public&q=" + parmSearch + "&app_id=9e739484&app_key=e3b4d6f7a98479690a4e75d907cd721c%09"
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

                                val rec = item["recipe"]

                                val name = (rec as JSONObject).get("label")

                                list.add(name.toString())
                            }
                        }
                    }
                }
            }
            catch (e : Exception)
            {
                println(e.message)
            }

            return list;
        }
    }
}