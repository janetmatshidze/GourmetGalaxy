package com.example.gourmetgalaxy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.gourmetgalaxy.databinding.FragmentNotificationsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        // Fetch and display notifications from Firestore
        fetchNotifications()

        return binding.root
    }

    private fun fetchNotifications() {
        db.collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                binding.notificationsContainer.removeAllViews() // Clear existing views

                for (document in documents) {
                    val title = document.getString("title") ?: "Notification"
                    val message = document.getString("message") ?: "No details available."

                    // Create a view for each notification with the actual title and message
                    val notificationView = createNotificationView(title, message)
                    binding.notificationsContainer.addView(notificationView)
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace() // Log error for debugging
            }
    }

    private fun createNotificationView(title: String, message: String): View {
        val context = requireContext()
        val notificationView = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 24, 24, 24)
            setBackgroundResource(R.drawable.notification_background)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16)
            }
        }

        val titleView = TextView(context).apply {
            text = title
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setTextColor(ContextCompat.getColor(context, R.color.titleColor))
        }

        val messageView = TextView(context).apply {
            text = message
            textSize = 16f
            setTextColor(ContextCompat.getColor(context, R.color.messageColor))
        }

        val timestampView = TextView(context).apply {
            text = "Time: " + SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault()).format(Date())
            textSize = 12f
            setTextColor(ContextCompat.getColor(context, R.color.timestampColor))
        }

        notificationView.apply {
            addView(titleView)
            addView(messageView)
            addView(timestampView)
        }

        return notificationView
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
