package com.example.gourmetgalaxy

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.method.HideReturnsTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FirebaseUser
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan

class SignUpFragment : Fragment() {

    companion object {
        const val RC_SIGN_UP = 9002
    }

    private var isPasswordVisible: Boolean = false
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Replace with your client ID
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText=view.findViewById<EditText>(R.id.ConfirmPasswordEditText)
        val firstNameEditText = view.findViewById<EditText>(R.id.firstnameditTextText)
        val lastNameEditText = view.findViewById<EditText>(R.id.lastnameEditText)
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val signUpButton = view.findViewById<Button>(R.id.SignUpbutton)
        val googleSignUpBtn = view.findViewById<ImageButton>(R.id.googleImageButton)
        val imageView5 = view.findViewById<ImageView>(R.id.imageView5)
        val agreeCheckBox = view.findViewById<CheckBox>(R.id.AgreeCheckBox)

        val fullText = "I agree to the Terms and Conditions and Privacy Policy"
        val spannableString = SpannableString(fullText)
        val tealColor = Color.parseColor("#008080")
        val termsStart = fullText.indexOf("Terms and Conditions")
        val termsEnd = termsStart + "Terms and Conditions".length
        spannableString.setSpan(
            ForegroundColorSpan(tealColor),
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val privacyStart = fullText.indexOf("Privacy Policy")
        val privacyEnd = privacyStart + "Privacy Policy".length
        spannableString.setSpan(
            ForegroundColorSpan(tealColor),
            privacyStart,
            privacyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        agreeCheckBox.text = spannableString
        // Password visibility toggle
        imageView5.setOnClickListener {
            if (isPasswordVisible) {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                imageView5.setImageResource(R.drawable.img_password_close)
            } else {
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                imageView5.setImageResource(R.drawable.img_password_open)
            }
            isPasswordVisible = !isPasswordVisible
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        // Sign-Up Button Click
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword=confirmPasswordEditText.text.toString().trim()
            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty() && agreeCheckBox.isChecked) {
                // Check password validity
                if (!isPasswordValid(password)) {
                    Toast.makeText(context, "Password must be at least 8 characters long, contain at least one uppercase letter, one digit, and one special character.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            // Add user info to the database (Firebase real-time database or Firestore)
                            Toast.makeText(context, "Sign-up successful! Please sign in.", Toast.LENGTH_SHORT).show()

                            // Clear the text fields after successful sign-up
                            emailEditText.text.clear()
                            passwordEditText.text.clear()
                            confirmPasswordEditText.text.clear()
                            firstNameEditText.text.clear()
                            lastNameEditText.text.clear()
                            agreeCheckBox.isChecked = false // Optionally reset the checkbox

                            // Switch to the sign-in tab
                            activity?.let {
                                if (it is TabbedActivity) {
                                    it.viewPager2.setCurrentItem(0, true) // Assuming sign-in fragment is at index 0
                                }
                            }
                        } else {
                            Toast.makeText(context, "Sign-up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Please fill all fields and agree to terms", Toast.LENGTH_SHORT).show()
            }
        }

        // Google Sign-In Button Click
        googleSignUpBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_UP)
        }

        return view
    }

    // Handle Google Sign-In result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_UP) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(context, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    Toast.makeText(context, "Google sign-in successful!", Toast.LENGTH_SHORT).show()

                    // Redirect to DashboardActivity
                    val intent = Intent(activity, DashboardActivity::class.java)
                    startActivity(intent)
                    activity?.finish() // Optional: finish current activity to prevent going back to the sign-up screen

                } else {
                    Toast.makeText(context, "Google sign-in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun isPasswordValid(password: String): Boolean {
        // Password requirements
        val minLength = 8
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() } // Check for special characters

        return password.length >= minLength && hasUpperCase && hasDigit && hasSpecialChar
    }
}
