package com.example.gourmetgalaxy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class FingerprintFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_fingerprint, container, false)

        val fingerprintBack: View = view.findViewById(R.id.fingerprintback)

        fingerprintBack.setOnClickListener {

            findNavController().navigate(R.id.action_fingerprintFragment_to_settingsFragment)
        }
        return view
    }
}
