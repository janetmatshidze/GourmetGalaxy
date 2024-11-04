package com.example.gourmetgalaxy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.gourmetgalaxy.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private var itemNotificationView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Set up button to navigate to settings
        binding.TurnOnNotificationsBtn.setOnClickListener {
            findNavController().navigate(R.id.action_notificationsFragment_to_settingsnotificationsFragment)
        }

        // Update the UI visibility based on toggle states when fragment resumes
        updateUIVisibility(inflater)

        return view
    }

    override fun onResume() {
        super.onResume()
        // Reload notification data and update visibility when fragment resumes
        updateUIVisibility(layoutInflater)
    }

    private fun updateUIVisibility(inflater: LayoutInflater) {
        val preferences = requireActivity().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val newRecipesEnabled = preferences.getBoolean("newRecipesEnabled", false)
        val recipeRatingsEnabled = preferences.getBoolean("recipeRatingsEnabled", false)

        // Retrieve notifications from SharedPreferences
        val notifications = preferences.getStringSet("notifications", mutableSetOf()) ?: mutableSetOf()

        // Remove any existing notification view if it exists
        itemNotificationView?.let {
            binding.notificationsContainer.removeView(it)
            itemNotificationView = null
        }

        if (notifications.isNotEmpty() && (newRecipesEnabled || recipeRatingsEnabled)) {
            notifications.forEach { notification ->
                itemNotificationView = inflater.inflate(R.layout.item_notification, binding.notificationsContainer, false).apply {
                    findViewById<TextView>(R.id.title).text = "Gourmet Galaxy Notification"
                    findViewById<TextView>(R.id.message).text = notification
                    findViewById<TextView>(R.id.time).text = System.currentTimeMillis().toString() // Should be formatted properly
                }
                binding.notificationsContainer.addView(itemNotificationView)
            }

            // Hide the default prompt views
            binding.notificationImage.visibility = View.GONE
            binding.notificationTitle.visibility = View.GONE
            binding.notificationDescription.visibility = View.GONE
            binding.TurnOnNotificationsBtn.visibility = View.GONE
            binding.btnNotNow.visibility = View.GONE
        } else {
            // No notifications enabled, show the prompt to turn on notifications
            binding.notificationImage.visibility = View.VISIBLE
            binding.notificationTitle.visibility = View.VISIBLE
            binding.notificationDescription.visibility = View.VISIBLE
            binding.TurnOnNotificationsBtn.visibility = View.VISIBLE
            binding.btnNotNow.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}