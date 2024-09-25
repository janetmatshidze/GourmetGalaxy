package com.example.gourmetgalaxy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class EditProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        val editprofileImageBtn: View = view.findViewById(R.id.editprofileBtn)
        editprofileImageBtn.setOnClickListener {

            findNavController().navigate(R.id.action_editprofileFragemnt_to_settingsFragment)
        }

        return view
    }
}