package com.example.gourmetgalaxy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.navigation.fragment.findNavController
import java.util.*

class LanguageFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_language, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        val radioEnglish: CheckedTextView = view.findViewById(R.id.radio_english)
        val radioAfrikaans: CheckedTextView = view.findViewById(R.id.radio_afrikaans)
        val radioZulu: CheckedTextView = view.findViewById(R.id.radio_zulu)

        // Load saved language setting and set the selected radio button
        val currentLanguage = sharedPreferences.getString("language", "en") ?: "en"
        updateCheckedState(currentLanguage, radioEnglish, radioAfrikaans, radioZulu)

        // Set click listeners for CheckedTextView items
        radioEnglish.setOnClickListener {
            updateLanguageSelection("en", radioEnglish, radioAfrikaans, radioZulu)
        }
        radioAfrikaans.setOnClickListener {
            updateLanguageSelection("af", radioEnglish, radioAfrikaans, radioZulu)
        }
        radioZulu.setOnClickListener {
            updateLanguageSelection("zu", radioEnglish, radioAfrikaans, radioZulu)
        }

        val languageBack: View = view.findViewById(R.id.languageback)
        languageBack.setOnClickListener {
            findNavController().navigate(R.id.action_languageFragment_to_settingsFragment)
        }

        return view
    }

    private fun updateLanguageSelection(
        languageCode: String,
        radioEnglish: CheckedTextView,
        radioAfrikaans: CheckedTextView,
        radioZulu: CheckedTextView
    ) {
        val currentLanguage = sharedPreferences.getString("language", "en")
        if (currentLanguage != languageCode) {
            setLocale(languageCode)
            updateCheckedState(languageCode, radioEnglish, radioAfrikaans, radioZulu)

            // Restart the whole app to apply the new language
            val intent = Intent(requireActivity().applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            requireActivity().applicationContext.startActivity(intent)
            requireActivity().finishAffinity()
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        Log.d("LanguageFragment", "Locale set to: $languageCode")

        // Update the app's configuration
        val config = requireActivity().resources.configuration
        config.setLocale(locale)
        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)

        // Save the selected language
        sharedPreferences.edit().putString("language", languageCode).apply()
    }

    private fun updateCheckedState(
        language: String,
        radioEnglish: CheckedTextView,
        radioAfrikaans: CheckedTextView,
        radioZulu: CheckedTextView
    ) {
        radioEnglish.isChecked = language == "en"
        radioAfrikaans.isChecked = language == "af"
        radioZulu.isChecked = language == "zu"
    }
}
