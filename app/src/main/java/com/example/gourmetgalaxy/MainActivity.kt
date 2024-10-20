package com.example.gourmetgalaxy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : BaseActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageButton = findViewById<ImageButton>(R.id.imageButton)

        imageButton.setOnClickListener {
            // Navigate to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            Log.d("MainActivity", "ImageButton clicked!")
        }
    }

    // Override the attachBaseContext method to apply the locale
    override fun attachBaseContext(newBase: Context?) {
        val language = newBase?.getSharedPreferences("app_prefs", MODE_PRIVATE)
            ?.getString("language", "en") ?: "en"
        val context = LocaleHelper.updateLocale(newBase!!, language) // Use updateLocale instead of wrap
        super.attachBaseContext(context)
    }
}
