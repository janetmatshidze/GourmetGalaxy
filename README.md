GourmetGalaxy

GourmetGalaxy is an Android application that provides users with a collection of recipes, a shopping list feature, and various customization options. It includes advanced features like fingerprint authentication, favorites management, offline capabilities, and Firebase integration. The app supports English and Zulu, catering to South African users with localized content, and includes real-time notifications for updates and alerts.
Table of Contents

    Project Overview
    Features
    Setup and Installation
    Dependencies
    Usage
    Authentication Options
    Troubleshooting
    License

Project Overview

The goal of GourmetGalaxy is to simplify recipe management and shopping for users. Features include:

    Recipe discovery and display
    Favorite recipe management
    Secure biometric authentication
    Firebase integration for data storage
    In-app language support (English and Zulu)
    Real-time push notifications for updates
    Offline access for improved usability

Features

    Recipe Display: View recipes from Firebase, with detailed views and filtering options.
    Favorites Management: Bookmark and manage favorite recipes.
    Biometric Authentication: Secure access using fingerprint authentication.
    Shopping List: Create and manage shopping lists, with "In Your List" and "Purchased" tracking.
    Language Settings: Interface available in both English and Zulu.
    Real-Time Notifications: Push notifications for updates and alerts.
    Offline Support: Access recipes and lists even without internet.
    SSO Integration: Support for Google and Facebook Single Sign-On.

Setup and Installation
Prerequisites

    Android Studio: Version 4.1 or later.
    Android SDK: Ensure adb (Android Debug Bridge) is accessible in your PATH.
    Firebase Project: Set up a Firebase project and connect it to your app.
    Facebook and Google API Credentials: Configure the Facebook and Google sign-in APIs.
    Biometric Hardware: Required for testing fingerprint authentication.

Steps

    Clone the Repository:

    bash

    git clone <repository_url>

    Open Project in Android Studio:
        Launch Android Studio and open the cloned project.
    Set Up Firebase:
        Add google-services.json to the app directory.
        Configure Firebase Authentication, Firestore, and Cloud Messaging.
    Add Facebook and Google Sign-In Credentials:
        For Facebook: Obtain an App ID and App Secret from the Facebook Developer Console and add them to strings.xml.
        For Google: Configure OAuth consent and API credentials in Google Cloud Console and integrate them in the project.
    Run the App:
        Select a virtual device or physical device and click Run.

Dependencies

The project uses the following dependencies:

    Firebase: Firebase Firestore, Firebase Auth, Firebase Cloud Messaging
    Biometric Authentication: androidx.biometric:biometric
    RecyclerView and CardView: For displaying lists and cards.
    Glide: For image loading in recipe displays.
    ViewModel and LiveData: For managing UI-related data.

Example libs.versions.toml

Ensure libs.versions.toml includes:

toml

[versions]
androidx-biometrics = "1.2.0"
firebase-auth = "21.0.1"
firebase-firestore = "23.0.3"
firebase-messaging = "22.0.0"
glide = "4.12.0"
lifecycle-viewmodel = "2.4.0"

Usage
Fingerprint Authentication

    Go to Settings > Security on your device to add a fingerprint.
    Open the app, which will prompt for fingerprint authentication before accessing secure sections.

Language Customization

    Access Settings within the app to switch between English and Zulu.

Real-Time Notifications

    Receive real-time alerts for recipe updates and personalized notifications.

Adding Recipes to Favorites

    Tap the bookmark icon on a recipe to add it to favorites, accessible in the Favorites section.

Shopping List Management

    Use the AddIngredientsFragment to search for ingredients, add them to your shopping list, and mark items as "Purchased."

Authentication Options

GourmetGalaxy provides multiple ways to sign in:

    Single Sign-On (SSO) with Google and Facebook:
        Select either Google Sign-In or Facebook Sign-In for quick, secure access using existing credentials.
        Both options are available on the login screen.
    Standard Sign-Up and Sign-In:
        Users can register directly within the app by providing an email and password.
        After registration, use the standard sign-in option to access the app.

Troubleshooting

    ADB Not Recognized: Ensure adb is added to your PATH, or run adb commands from the SDK’s platform-tools directory.
    Firebase Authentication Error: Verify that google-services.json is correctly added, and Firebase is set up correctly.
    Push Notifications Not Working: Confirm Firebase Cloud Messaging is configured, and notifications are enabled for the app.
    Biometric Prompt Not Showing: Ensure that the device supports fingerprint hardware and that at least one fingerprint is registered.

License

This project is licensed under the MIT License. See the LICENSE file for details.
