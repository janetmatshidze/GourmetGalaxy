package com.example.gourmetgalaxy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController

class HelpAndSupportFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_help_and_support, container, false)

        val backButton: ImageButton = view.findViewById(R.id.helpBackButton)

        backButton.setOnClickListener {
            findNavController().navigateUp() // Navigate back to settings fragment
        }

        // Sets up a click listener on the phone number to initiate a call
        view.findViewById<View>(R.id.phoneTextView).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+27731681659")
            }
            startActivity(intent)
        }

        // Sets up a click listener on the email address to open an email client
        view.findViewById<View>(R.id.emailTextView).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@gourmetgalaxy.com")
            }
            startActivity(intent)
        }

        return view
    }
}
