package com.example.gourmetgalaxy

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

class GourmetGalaxyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize the Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
    }
}