package com.example.gourmetgalaxy

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import android.provider.MediaStore
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController
import java.io.File
import java.io.FileOutputStream

class EditProfileFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var profileImageView: CircleImageView
    private lateinit var editNameTextView: TextView
    private lateinit var joinedDateTextView: TextView

    // Constants for image selection
    private val PICK_IMAGE_REQUEST = 1
    private val TAKE_PHOTO_REQUEST = 2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        mAuth = FirebaseAuth.getInstance()

        profileImageView = view.findViewById(R.id.edit_profile_image)
        editNameTextView = view.findViewById(R.id.edit_name)
        joinedDateTextView = view.findViewById(R.id.joined_date_display)
        val selectDateButton: View = view.findViewById(R.id.select_joined_date_btn)
        val editProfileImageBtn: ImageButton = view.findViewById(R.id.editprofileBtn)

        editProfileImageBtn.setOnClickListener {
            findNavController().navigate(R.id.action_editprofileFragemnt_to_settingsFragment)
        }

        // Load current user data
        val user = mAuth.currentUser
        if (user != null) {
            // Load profile picture from Firebase or placeholder
            val photoUrl = user.photoUrl
            if (photoUrl != null) {
                Glide.with(this)
                    .load(photoUrl)
                    .placeholder(R.drawable.img_profile2) // Default image
                    .into(profileImageView)
            } else {
                // Load a default image if no profile picture exists
                profileImageView.setImageResource(R.drawable.img_profile2)
            }

            // Set display name and joined date
            val displayName = user.displayName ?: "User"
            editNameTextView.setText(displayName) // Set as editable TextInput
            joinedDateTextView.text = "Joined: ${formatDate(user.metadata?.creationTimestamp)}"
        }

        // Image picker for changing profile picture
        profileImageView.setOnClickListener {
            // Show options to choose an image from the gallery or take a photo
            showImagePickerOptions()
        }

        selectDateButton.isEnabled = false // Disable editing joined date

        return view
    }

    private fun showImagePickerOptions() {
        // You can create an AlertDialog to choose between Gallery and Camera
        val options = arrayOf("Gallery", "Camera")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openImageChooser() // Gallery
                1 -> openCamera() // Camera
            }
        }
        builder.show()
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, TAKE_PHOTO_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        updateProfileImage(it)
                    }
                }
                TAKE_PHOTO_REQUEST -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        // Convert bitmap to Uri
                        val imageUri = saveImageToExternalStorage(it)
                        updateProfileImage(imageUri)
                    }
                }
            }
        }
    }

    private fun updateProfileImage(imageUri: Uri) {
        // Set the new image in CircleImageView
        profileImageView.setImageURI(imageUri)

        // Optionally, upload the new image to Firebase Storage and update the user's profile
        uploadProfileImage(imageUri)
    }

    private fun saveImageToExternalStorage(bitmap: Bitmap): Uri {
        val filename = "${System.currentTimeMillis()}.jpg"
        val fos: FileOutputStream
        try {
            // Create a file in the external storage directory
            val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(storageDir, filename)
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            return Uri.fromFile(file) // Return the Uri of the saved file
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle error and return a default Uri if necessary
            return Uri.EMPTY // Or handle error appropriately
        }
    }

    private fun uploadProfileImage(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${mAuth.currentUser?.uid}.jpg")

        storageRef.putFile(imageUri).addOnSuccessListener {
            // Get the URL of the uploaded image
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                updateUserProfile(uri)
            }
        }.addOnFailureListener {
            // Handle failures
        }
    }

    private fun updateUserProfile(imageUri: Uri) {
        val user = mAuth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(imageUri) // Set the new photo URL in the user's Firebase profile
            .build()

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Successfully updated profile image in Firebase
                Glide.with(this)
                    .load(imageUri)
                    .into(profileImageView)
            }
        }
    }

    private fun formatDate(timestamp: Long?): String {
        return timestamp?.let {
            val dateFormat = android.text.format.DateFormat.getMediumDateFormat(requireContext())
            dateFormat.format(it)
        } ?: "Unknown"
    }
}
