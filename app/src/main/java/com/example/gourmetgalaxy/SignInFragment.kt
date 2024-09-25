package com.example.gourmetgalaxy

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView


class SignInFragment : Fragment() {
    private var isPasswordVisible: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_signin, container, false)

        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val imageView5 = view.findViewById<ImageView>(R.id.imageView5)
        val signInBtn=view.findViewById<Button>(R.id.SignInbutton)


        imageView5.setOnClickListener {
            isPasswordVisible = if (isPasswordVisible) {

                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                imageView5.setImageResource(R.drawable.img_password_close)
                false
            } else {

                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imageView5.setImageResource(R.drawable.img_password_open)
                true
            }

            passwordEditText.setSelection(passwordEditText.text.length)
        }
        signInBtn.setOnClickListener {
            val intent = Intent(activity, DashboardActivity::class.java)
            startActivity(intent)
        }


        return view
    }
}
