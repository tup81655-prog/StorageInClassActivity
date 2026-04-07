package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import androidx.core.content.edit

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        showButton.setOnClickListener {
            val input = numberEditText.text.toString()
            if (input.isNotEmpty()) {
                downloadComic(input)
            } else {
                Toast.makeText(this, "Enter a comic number", Toast.LENGTH_SHORT).show()
            }
        }

        loadSavedComic()


    }

    // Fetches comic from web as JSONObject
    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                showComic(response)
                saveComic(response) // save after download
            }
            ,
            { error ->
                Toast.makeText(this, "Error loading comic", Toast.LENGTH_SHORT).show()
            }
        )

        requestQueue.add(request)
    }

    // Display a comic for a given comic JSON object
    private fun showComic(comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    // Implement this function
    private fun saveComic(comicObject: JSONObject) {
        val sharedPref = getSharedPreferences("comic_prefs", MODE_PRIVATE)
        sharedPref.edit {

            putString("title", comicObject.getString("title"))
            putString("alt", comicObject.getString("alt"))
            putString("img", comicObject.getString("img"))
            putInt("num", comicObject.getInt("num"))

        }
    }

    private fun loadSavedComic() {
        val sharedPref = getSharedPreferences("comic_prefs", MODE_PRIVATE)

        if (sharedPref.contains("title")) {
            titleTextView.text = sharedPref.getString("title", "")
            descriptionTextView.text = sharedPref.getString("alt", "")

            val imageUrl = sharedPref.getString("img", null)
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).into(comicImageView)
            }
        }
    }
}