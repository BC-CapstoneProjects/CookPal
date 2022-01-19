package com.example.testrecipeapi

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btnSearch)

        btnSearch.setOnClickListener {
            val txtSearchVal = findViewById<TextInputEditText>(R.id.txtSearch)

            val str = txtSearchVal.text.toString()

            doAsync {
                search(str)
            }.execute()

            hideKeyboard()
        }
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun search(searchval : String)
    {
        try {

            var parmSearch = searchval

            parmSearch.replace(" ", "%20")

            var list = mutableListOf("")

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

            val txtRes = findViewById<TextView>(R.id.txtResult)

            this.runOnUiThread(

                java.lang.Runnable {

                    var resultStr = ""

                    for (item : String in list)
                    {
                        resultStr += item + "\n"
                    }

                    txtRes.text = resultStr
                }
            )
        }
        catch (e : Exception)
        {
            println(e.message)
        }
    }
}

class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}

