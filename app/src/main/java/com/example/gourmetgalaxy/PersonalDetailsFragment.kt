package com.example.gourmetgalaxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PersonalDetailsFragment : Fragment() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var phoneEditText: TextInputEditText
    private lateinit var addressEditText: TextInputEditText
    private lateinit var saveButton: Button

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_personal_details, container, false)


        val backButton: ImageButton = view.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            findNavController().navigateUp() // Navigate back
        }

        emailEditText = view.findViewById(R.id.editTextEmail)
        phoneEditText = view.findViewById(R.id.editTextPhone)
        addressEditText = view.findViewById(R.id.editTextAddress)
        saveButton = view.findViewById(R.id.buttonSave)

        // Load user data if available
        loadUserData()

        // Save button click listener
        saveButton.setOnClickListener {
            saveUserData()
        }

        return view
    }

    private fun loadUserData() {
        // Retrieve email from FirebaseAuth
        val user = auth.currentUser
        user?.let {
            emailEditText.setText(it.email)
        }

        // Retrieve additional details (address, phone) from Firestore
        user?.uid?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        phoneEditText.setText(document.getString("phone"))
                        addressEditText.setText(document.getString("address"))
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors (optional)
                }
        }
    }

    private fun saveUserData() {
        // Save phone and address to Firestore
        val phone = phoneEditText.text.toString()
        val address = addressEditText.text.toString()

        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            val userData = hashMapOf(
                "phone" to phone,
                "address" to address
            )

            firestore.collection("users").document(userId).set(userData)
                .addOnSuccessListener {
                    // Data successfully saved (optional: show a confirmation)
                }
                .addOnFailureListener { exception ->
                    // Handle any errors (optional)
                }
        }
    }
}
