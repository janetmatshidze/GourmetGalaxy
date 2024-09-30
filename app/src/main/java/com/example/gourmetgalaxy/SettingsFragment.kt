package com.example.gourmetgalaxy

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class SettingsFragment : Fragment() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance()

        // Initialize Google SignIn Client
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Setup UI elements and listeners
        val profileImageView: CircleImageView = view.findViewById(R.id.profile_image)
        val profileNameTextView: TextView = view.findViewById(R.id.profile_name)
        val profileJoinedTextView: TextView = view.findViewById(R.id.profile_joined)
        val fingerprintLayout: View = view.findViewById(R.id.fingerprintlayout)
        val languageLayout: View = view.findViewById(R.id.languagelayout)
        val notificationsLayout: View = view.findViewById(R.id.notificationslayout)
        val editprofileBtn: View = view.findViewById(R.id.editprofileImageBtn)
        val deleteAccountBtn: TextView = view.findViewById(R.id.delete_account)

        // Get the current user
        val user = mAuth.currentUser
        if (user != null) {
            var displayName = user.displayName

            // Check if display name is null and set a formatted default name or use a generic "User" name
            if (displayName.isNullOrEmpty()) {
                // Format email prefix as default display name (e.g., "Janet Matshidze" instead of "janetmatshidze")
                displayName = user.email?.split("@")?.get(0)?.replaceFirstChar { it.uppercase() }
                displayName =
                    displayName?.split(Regex("(?=\\p{Upper})"))?.joinToString(" ") { it.trim() }

                profileNameTextView.text = displayName ?: "User"

                // Optionally, update the user's profile with this display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(profileNameTextView.text.toString())
                    .build()
                user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // User profile updated successfully
                    }
                }
            } else {
                profileNameTextView.text = displayName
            }

            // Set joined date and profile image
            profileJoinedTextView.text =
                "Joined: ${user.metadata?.creationTimestamp?.let { formatDate(it) }}"
            Glide.with(this)
                .load(user.photoUrl)
                .placeholder(R.drawable.img_profile2) // Default image
                .into(profileImageView)

            // Check the sign-in method
            user.providerData.forEach { profile ->
                when (profile.providerId) {
                    "google.com" -> {
                        // User signed in with Google
                    }

                    "password" -> {
                        // User signed in with email/password
                    }
                }
            }
        }

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

        val signoutBtn = view.findViewById<TextView>(R.id.logout_button)
        signoutBtn.setOnClickListener {
            signOutAndStartSignInFragment()
        }

        // Handle delete account button click
        deleteAccountBtn.setOnClickListener {
            deleteAccount()
        }

        return view
    }

    private fun formatDate(timestamp: Long): String {
        // Convert timestamp to a readable date format (e.g., "Dec 28, 2020")
        val dateFormat = android.text.format.DateFormat.getMediumDateFormat(requireContext())
        return dateFormat.format(timestamp)
    }

    private fun signOutAndStartSignInFragment() {
        // Create an AlertDialog to confirm sign-out
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirm Sign Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { dialog, which ->
                // If user confirms, sign out from Firebase
                mAuth.signOut()

                // Sign out from Google if the user signed in with Google
                mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity()) {
                    // After sign out is complete, navigate to the TabbedActivity
                    val intent = Intent(requireActivity(), TabbedActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Dismiss the dialog
                dialog.dismiss()
            }

        // Show the dialog
        builder.create().show()
    }

    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser

        // First, delete the user from Firestore or other databases
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(user!!.uid)
            .delete()
            .addOnSuccessListener {
                // Now delete the user's authentication account from FirebaseAuth
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Account successfully deleted.",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Redirect to the sign-in screen or any other screen
                        val intent = Intent(requireActivity(), TabbedActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        // If delete failed (e.g., re-authentication needed)
                        Toast.makeText(
                            requireContext(),
                            "Failed to delete account. Please sign in again.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // Re-authenticate the user if required (for security reasons)
                        reAuthenticateUser()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to delete data: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // Function to handle re-authentication if required
    private fun reAuthenticateUser() {
        val user = FirebaseAuth.getInstance().currentUser
        val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())

        if (googleSignInAccount != null) {
            val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
            user?.reauthenticate(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Retry account deletion after re-authentication
                    deleteAccount()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Re-authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            // Handle re-authentication for other providers (Facebook, Email/Password, etc.)
        }
    }
}
