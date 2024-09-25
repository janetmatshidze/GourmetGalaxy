package com.example.gourmetgalaxy

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SignUpFragment : Fragment() {

    private var isPasswordVisible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val imageView5 = view.findViewById<ImageView>(R.id.imageView5)
        val agreeCheckBox = view.findViewById<CheckBox>(R.id.AgreeCheckBox)


        imageView5.setOnClickListener {
            isPasswordVisible = if (isPasswordVisible) {

                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                imageView5.setImageResource(R.drawable.img_password_close) // Update with your icon for hidden password
                false
            } else {

                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imageView5.setImageResource(R.drawable.img_password_open) // Update with your icon for visible password
                true
            }


            passwordEditText.setSelection(passwordEditText.text.length)
        }

        // Set different colors for "T&C" and "Privacy Policy" in the CheckBox text
        val text = "I agree with the T&Cs and Privacy Policy"
        val spannableString = SpannableString(text)

        // Set color for "T&C"
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.teal)),
            text.indexOf("T&Cs"),
            text.indexOf("T&Cs") + 4,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set color for "Privacy Policy"
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.teal)),
            text.indexOf("Privacy Policy"),
            text.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Apply the spannable string to the CheckBox
        agreeCheckBox.text = spannableString


        return view
    }
}
