package com.example.gourmetgalaxy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Date

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Retrieve the actual title and message from the notification data payload without fallback
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        // Check if both title and message exist in the data payload
        if (title != null && message != null) {
            // Save to Firestore
            saveNotificationToFirestore(title, message)

            // Display system tray notification
            showNotification(title, message)
        } else {
            // Log or handle the case where data payload is incomplete
            // This could be helpful for debugging but will not show any notification
            println("Notification data payload is missing title or message.")
        }
    }

    private fun saveNotificationToFirestore(title: String, message: String) {
        val db = FirebaseFirestore.getInstance()
        val notificationData = mapOf(
            "title" to title,
            "message" to message,
            "timestamp" to Date()
        )

        db.collection("notifications")
            .add(notificationData)
            .addOnSuccessListener {
                // Optional: Log success for debugging
            }
            .addOnFailureListener { e ->
                e.printStackTrace() // Log the error for debugging
            }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "gourmetgalaxy_notifications_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Gourmet Galaxy Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for receiving notifications about new recipes and ratings."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigateTo", "NotificationsFragment")
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = System.currentTimeMillis().toInt()
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.img_home2) // Replace with your notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Send the new token to your server if necessary
    }
}
