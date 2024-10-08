package com.example.gourmetgalaxy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class LanguageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_language, container, false)

        val languageBack: View = view.findViewById(R.id.languageback)

        languageBack.setOnClickListener {

            findNavController().navigate(R.id.action_languageFragment_to_settingsFragment)
        }
        return view
    }
}