package com.example.gourmetgalaxy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if the message contains data and extract it
        val title = remoteMessage.data["title"] ?: "New Notification"
        val message = remoteMessage.data["message"] ?: "You have a new update from Gourmet Galaxy!"

        // Save notification data in SharedPreferences for NotificationsFragment
        val preferences = getSharedPreferences("Notifications", Context.MODE_PRIVATE)
        preferences.edit().apply {
            putString("latest_title", title)
            putString("latest_message", message)
            apply()
        }

        // Display the notification to the user
        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "gourmetgalaxy_notifications_channel"

        // Create notification channel for Android O and above
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

        // Create an intent to open NotificationsFragment when the notification is tapped
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigateTo", "NotificationsFragment") // Pass extra to determine navigation
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build and display the notification
        val notificationId = System.currentTimeMillis().toInt() // Unique ID for each notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.img_home2) // Replace with your notification icon
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification) // Use unique ID
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Here you can send the token to your server if needed
    }
}
