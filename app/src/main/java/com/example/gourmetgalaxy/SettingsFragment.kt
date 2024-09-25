package com.example.gourmetgalaxy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class SettingsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)


        val fingerprintLayout: View = view.findViewById(R.id.fingerprintlayout)
        val languageLayout :View =view.findViewById(R.id.languagelayout)
        val notificationsLayout:View = view.findViewById(R.id.notificationslayout)
        val editprofileBtn:View=view.findViewById(R.id.editprofileImageBtn)

        fingerprintLayout.setOnClickListener {

            findNavController().navigate(R.id.action_settingsFragment_to_fingerprintFragment)
        }

        languageLayout.setOnClickListener {

            findNavController().navigate(R.id.action_settingsFragment_to_languageFragment)
        }
        notificationsLayout.setOnClickListener {

            findNavController().navigate(R.id.action_settingsFragment_to_settingsnotificationsFragment)
        }
        editprofileBtn.setOnClickListener {

            findNavController().navigate(R.id.action_settingsFragment_to_editprofileFragment)
        }
        return view
    }
}
