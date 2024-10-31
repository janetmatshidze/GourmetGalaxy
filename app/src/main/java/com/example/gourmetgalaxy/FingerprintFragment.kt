package com.example.gourmetgalaxy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class FingerprintFragment : Fragment() {

    private lateinit var addFingerprintButton: ImageButton
    private lateinit var fingerprintTextView: TextView
    private var fingerprintCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fingerprint, container, false)

        addFingerprintButton = view.findViewById(R.id.button_add_fingerprint)
        fingerprintTextView = view.findViewById(R.id.textView_add_fingerprint)

        val fingerprintBack: View = view.findViewById(R.id.fingerprintback)
        fingerprintBack.setOnClickListener {
            findNavController().navigate(R.id.action_fingerprintFragment_to_settingsFragment)
        }

        // Load stored fingerprint count from SharedPreferences
        fingerprintCount = loadFingerprintCount()

        // Update the fingerprint text based on count
        updateFingerprintText()

        // Add Fingerprint button action
        addFingerprintButton.setOnClickListener {
            if (isFingerprintAvailable()) {
                setupBiometricPrompt()
            } else {
                Toast.makeText(requireContext(), "Please enroll a fingerprint in Settings.", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                startActivity(intent)
            }
        }

        return view
    }

    private fun isFingerprintAvailable(): Boolean {
        val biometricManager = BiometricManager.from(requireContext())
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun setupBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(requireContext(), "Authentication successful!", Toast.LENGTH_SHORT).show()

                // Increment fingerprint count and save it
                fingerprintCount++
                saveFingerprintCount(fingerprintCount)

                // Update fingerprint text view
                updateFingerprintText()

                // Lock the app
                lockApp()

                // Navigate to MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish() // End fragment's activity to prevent back navigation
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(requireContext(), "Authentication failed. Try again.", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(requireContext(), "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Authenticate with Fingerprint")
            .setSubtitle("Use your fingerprint to authenticate")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun updateFingerprintText() {
        fingerprintTextView.text = if (fingerprintCount > 0) {
            "Fingerprint $fingerprintCount"
        } else {
            "Add Fingerprint"
        }
    }

    private fun loadFingerprintCount(): Int {
        val prefs = requireContext().getSharedPreferences("fingerprint_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("fingerprint_count", 0)
    }

    private fun saveFingerprintCount(count: Int) {
        val prefs = requireContext().getSharedPreferences("fingerprint_prefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putInt("fingerprint_count", count)
            apply()
        }
    }

    private fun lockApp() {
        val prefs = requireContext().getSharedPreferences("app_lock_prefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean("is_app_locked", true) 
            apply()
        }
    }
}