package com.example.gourmetgalaxy

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.*

class SignInFragment : Fragment() {

    companion object {
        const val RC_SIGN_IN = 9001
    }

    private var isPasswordVisible: Boolean = false
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signin, container, false)

        // Initializes Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Initializes Facebook SDK and CallbackManager
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    handleFacebookAccessToken(loginResult.accessToken.token)
                }

                override fun onCancel() {
                    Toast.makeText(activity, "Facebook login cancelled", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: FacebookException) {
                    Log.e("FacebookAuthError", "Facebook authentication failed: ${exception.message}")
                    Toast.makeText(activity, "Facebook login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            })

        // Configures Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Find views
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val imageView5 = view.findViewById<ImageView>(R.id.imageView5)
        val signInBtn = view.findViewById<Button>(R.id.SignInbutton)
        val googleSignInBtn = view.findViewById<ImageButton>(R.id.googleImageButton)
        val facebookSignInBtn = view.findViewById<ImageButton>(R.id.facebookImageButton)

        // Toggle password visibility
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

        // Sign-In Button Click
        signInBtn.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Check password validity
                if (!isPasswordValid(password)) {
                    Toast.makeText(context, "Password must be at least 8 characters long, contain at least one uppercase letter, one digit, and one special character.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // Check if the email is already associated with an existing provider
                auth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val signInMethods = task.result?.signInMethods
                            if (signInMethods?.isNotEmpty() == true) {
                                // The email is already in use
                                if (signInMethods.contains(GoogleAuthProvider.GOOGLE_SIGN_IN_METHOD)) {
                                    // Prompt user to sign in with Google
                                    Toast.makeText(context, "Email is already linked with a Google account. Please sign in with Google.", Toast.LENGTH_SHORT).show()
                                    signInWithGoogle() // Initiate Google sign-in
                                } else {
                                    // The email is linked with a different provider
                                    Toast.makeText(context, "Email is already in use by another provider. Please use that provider to sign in.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // No existing provider, proceed with manual sign-up
                                createManualAccount(email, password)
                            }
                        } else {
                            Toast.makeText(context, "Failed to check sign-in methods: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Please fill in both email and password", Toast.LENGTH_SHORT).show()
            }
        }

        facebookSignInBtn.setOnClickListener {
            // Login to Facebook
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        }

        // Set up Google Sign-In button click listener
        googleSignInBtn.setOnClickListener {
            signInWithGoogle()
        }

        return view
    }

    private fun createManualAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Sign-up successful!", Toast.LENGTH_SHORT).show()

                    // Navigate to the DashboardActivity
                    val intent = Intent(activity, DashboardActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                } else {
                    Toast.makeText(context, "Sign-up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun handleFacebookAccessToken(token: String) {
        val facebookCredential = FacebookAuthProvider.getCredential(token)

        auth.signInWithCredential(facebookCredential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(activity, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()

                    val intent = Intent(activity, DashboardActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Log.e("FirebaseAuthError", "Facebook authentication failed: ${task.exception?.message}", task.exception)
                    Toast.makeText(activity, "Facebook authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Handles the result from Google Sign-in
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val googleCredential = GoogleAuthProvider.getCredential(account.idToken, null)

                // Sign in with Google credential
                auth.signInWithCredential(googleCredential)
                    .addOnCompleteListener(requireActivity()) { googleTask ->
                        if (googleTask.isSuccessful) {
                            Toast.makeText(activity, "Google Sign-in successful!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, DashboardActivity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        } else {
                            Log.e("GoogleAuthError", "Google authentication failed: ${googleTask.exception?.message}")
                            Toast.makeText(activity, "Google authentication failed: ${googleTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                Log.e("GoogleSignInError", "Google sign-in failed: ${e.message}")
                Toast.makeText(activity, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        // Password requirements
        val minLength = 8
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }

        return password.length >= minLength && hasUpperCase && hasDigit && hasSpecialChar
    }
}
