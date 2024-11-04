package com.example.gourmetgalaxy

import android.Manifest
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.util.UUID

class AddRecipeFragment : Fragment() {

    private lateinit var viewModel: RecipeViewModel
    private lateinit var selectedImageUri: Uri
    private lateinit var uploadLayout: LinearLayout
    private lateinit var uploadImage: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    // ActivityResultLauncher for picking an image from the gallery
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            uploadImage.setImageURI(it)
        }
    }

    // ActivityResultLauncher for capturing a photo using the camera
    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
        if (success) {
            uploadImage.setImageURI(selectedImageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_addrecipe, container, false)

        val backButton: ImageButton = view.findViewById(R.id.backButton)

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_addrecipeFragment_to_recipeFragment)
        }
        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        // Set up the adapter for Meal Course (AutoCompleteTextView)
        val categories = resources.getStringArray(R.array.categories_array)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)

        // Get the AutoCompleteTextView from the TextInputLayout
        val autoCompleteTextView = view.findViewById<TextInputLayout>(R.id.menu).editText as? AutoCompleteTextView
        autoCompleteTextView?.setAdapter(adapter)

        viewModel = ViewModelProvider(requireActivity()).get(RecipeViewModel::class.java)

        val editRecipe = view.findViewById<TextInputEditText>(R.id.editRecipe)
        val editDescription = view.findViewById<TextInputEditText>(R.id.editDescription)
        val editDuration = view.findViewById<TextInputEditText>(R.id.editDuration)
        val editIngredients = view.findViewById<TextInputEditText>(R.id.editIngredients)
        val editInstructions = view.findViewById<TextInputEditText>(R.id.editInstructions)
        val addRecipeButton = view.findViewById<Button>(R.id.AddRecipeButton)

        uploadLayout = view.findViewById(R.id.uploadlayout)
        uploadImage = view.findViewById(R.id.upload_image)

        // Set up click listener for upload layout
        uploadLayout.setOnClickListener {
            showImagePickerDialog()
        }

        // Show TimePickerDialog on clicking the clock icon in Duration field
        editDuration.setOnClickListener {
            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    editDuration.setText("$hourOfDay h $minute m")
                },
                0, 0, true
            )
            timePicker.show()
        }

        addRecipeButton.setOnClickListener {
            val mealCourse = autoCompleteTextView?.text.toString() // Get the selected meal course

            // Create a new recipe object without the userId field
            val recipe = Recipe(
                id = UUID.randomUUID().toString(),
                title = editRecipe.text.toString(),
                description = editDescription.text.toString(),
                duration = editDuration.text.toString(),
                ingredients = editIngredients.text.toString(),
                instructions = editInstructions.text.toString(),
                imageUri = null, // Set the image URI if available
                mealCourse = mealCourse // Include the meal course
            )

            uploadImageToFirebase(recipe) // Proceed with uploading the recipe
        }



        return view
    }



    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // Take a photo using camera
                    capturePhoto()
                }
                1 -> {
                    // Choose image from gallery
                    pickImageLauncher.launch("image/*")
                }
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun capturePhoto() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            val imageFile = File.createTempFile("recipe_image_", ".jpg", requireContext().cacheDir)
            selectedImageUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                imageFile
            )
            takePhotoLauncher.launch(selectedImageUri)
        }
    }

    private fun uploadImageToFirebase(recipe: Recipe) {
        val imageRef = storage.reference.child("recipe_images/${recipe.id}.jpg")
        imageRef.putFile(selectedImageUri).addOnSuccessListener {
            // Get the download URL and save the recipe
            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                recipe.imageUri = downloadUri.toString() // Update image URI with the download URL
                saveRecipeToFirestore(recipe)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveRecipeToFirestore(recipe: Recipe) {
        firestore.collection("recipes").document(recipe.id)
            .set(recipe)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Recipe added successfully", Toast.LENGTH_SHORT).show()
                saveNotification("New recipe added: ${recipe.title}") // Save notification
                findNavController().navigateUp() // Navigate back to HomeFragment
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to add recipe: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveNotification(message: String) {
        val preferences = requireActivity().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val notifications = preferences.getStringSet("notifications", mutableSetOf()) ?: mutableSetOf()
        notifications.add(message)
        preferences.edit().putStringSet("notifications", notifications).apply()
    }

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 1001
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhoto()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
