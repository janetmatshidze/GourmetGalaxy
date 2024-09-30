import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gourmetgalaxy.Recipe
import com.google.firebase.firestore.FirebaseFirestore

class RecipeViewModel : ViewModel() {
    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> = _recipes

    private val firestore = FirebaseFirestore.getInstance()

    fun fetchRecipes() {
        firestore.collection("recipes")
            .get()
            .addOnSuccessListener { result ->
                val recipeList = mutableListOf<Recipe>()
                for (document in result) {
                    val recipe = document.toObject(Recipe::class.java)
                    recipeList.add(recipe)
                }
                _recipes.value = recipeList // Update LiveData with the fetched recipes
            }
            .addOnFailureListener { e ->
                // Handle errors
            }
    }
}
