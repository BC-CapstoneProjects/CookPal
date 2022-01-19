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
            val txtRes = findViewById<TextView>(R.id.txtResult)
            val str = txtSearchVal.text.toString()

            doAsync {
                var list = RecipeAPI.search(str)

                this.runOnUiThread(

                    java.lang.Runnable {

                        var resultStr = ""

                        for (item: String in list) {
                            resultStr += item + "\n"
                        }

                        txtRes.text = resultStr
                    }
                )
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
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}

class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}

