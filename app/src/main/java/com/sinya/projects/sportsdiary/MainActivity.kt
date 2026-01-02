package com.sinya.projects.sportsdiary

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sinya.projects.sportsdiary.main.MainApp
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MainApp(updateLocale = { lang -> updateLocale(lang) })
        }
    }

    private fun updateLocale( language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}


//val client = OkHttpClient()
//
//val request = Request.Builder()
//    .url("https://exercisedb-api1.p.rapidapi.com/api/v1/exercises/search?search=strength%20exercises")
//    .get()
//    .addHeader("x-rapidapi-key", "e98b21dd7fmshf921a8d7f08ce5cp1243bcjsn79a6aa3682cb")
//    .addHeader("x-rapidapi-host", "exercisedb-api1.p.rapidapi.com")
//    .build()
//
//val response = client.newCall(request).execute()
