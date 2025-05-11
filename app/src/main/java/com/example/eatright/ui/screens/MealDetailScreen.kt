package com.example.eatright.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.eatright.data.api.RetrofitInstance
import com.example.eatright.data.model.Meal
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(mealId: String, navController: NavController) {
    var meal by remember { mutableStateOf<Meal?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val db = Firebase.firestore

    //DG function to get map ingredients and filter null ingredients
    fun getIngredientsList(): List<Pair<String,String?>> {
        val ingredients = mutableListOf<Pair<String, String?>>()
        val ingredientMap = mapOf(
            meal?.strIngredient1 to meal?.strMeasure1, meal?.strIngredient2 to meal?.strMeasure2,
            meal?.strIngredient3 to meal?.strMeasure3, meal?.strIngredient4 to meal?.strMeasure4,
            meal?.strIngredient5 to meal?.strMeasure5, meal?.strIngredient6 to meal?.strMeasure6,
            meal?.strIngredient7 to meal?.strMeasure7, meal?.strIngredient8 to meal?.strMeasure8,
            meal?.strIngredient9 to meal?.strMeasure9, meal?.strIngredient10 to meal?.strMeasure10,
            meal?.strIngredient11 to meal?.strMeasure11, meal?.strIngredient12 to meal?.strMeasure12,
            meal?.strIngredient13 to meal?.strMeasure13, meal?.strIngredient14 to meal?.strMeasure14,
            meal?.strIngredient15 to meal?.strMeasure15, meal?.strIngredient16 to meal?.strMeasure16,
            meal?.strIngredient17 to meal?.strMeasure17, meal?.strIngredient18 to meal?.strMeasure18,
            meal?.strIngredient19 to meal?.strMeasure19, meal?.strIngredient20 to meal?.strMeasure20
        )

        for ((name, measure) in ingredientMap) {
            if (!name.isNullOrBlank()) {
                ingredients.add(Pair(name, measure ?: ""))
            }
        }
        return ingredients
    }

    //DG Fucntion return a string of ingredients ready for display on screen
    fun quantity():String {
        val foodMeasure = getIngredientsList()
        var textScript = "Ingredients: \n"
        for (pair in foodMeasure) {
            textScript += "${pair.second} ${pair.first}  \n"
        }
        return textScript
    }

    LaunchedEffect(mealId) {
        scope.launch {
            try {
                val response = RetrofitInstance.api.getMealById(mealId)
                meal = response.meals?.firstOrNull()
                isError = meal == null
            } catch (e: Exception) {
                e.printStackTrace()
                isError = true
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Meal Details") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                isError -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Failed to load meal details.")
                    }
                }
                meal != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Meal Image
                        Image(
                            painter = rememberAsyncImagePainter(meal!!.imageUrl),
                            contentDescription = meal!!.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .padding(bottom = 16.dp)
                        )

                        // Meal Name
                        Text(
                            text = meal!!.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontSize = 28.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Meal Category and Area
                        Text(
                            text = "Category: ${meal!!.category ?: "N/A"}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = "Area: ${meal!!.area ?: "N/A"}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        //DG Displaying list of ingredients below:
                        Text(
                            text = quantity(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Instructions Title
                        Text(
                            text = "Instructions",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = meal!!.instructions ?: "Instructions coming soon 🍴",
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // ❤️ Save to Favorites Button
                        Button(
                            onClick = {
                                val favoriteMeal = hashMapOf(
                                    "id" to meal!!.id,
                                    "name" to meal!!.name,
                                    "imageUrl" to meal!!.imageUrl
                                )
                                db.collection("favorites")
                                    .document(meal!!.id)
                                    .set(favoriteMeal)
                                    .addOnSuccessListener {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("✅ Meal added to Favorites")
                                        }
                                    }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),

                                    modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Save to Favorites ❤️")
                        }
                    }
                }
            }
        }
    }
}
