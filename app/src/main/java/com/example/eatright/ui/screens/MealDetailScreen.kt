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
