package com.example.gourmetgalaxy

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.FirebaseApp

class GourmetGalaxyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize the Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}