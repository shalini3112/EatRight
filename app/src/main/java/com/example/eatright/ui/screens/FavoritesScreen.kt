package com.example.eatright.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

data class FavoriteMeal(
    val id: String,
    val name: String,
    val imageUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    var favoriteMeals by remember { mutableStateOf<List<FavoriteMeal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var mealToDelete by remember { mutableStateOf<FavoriteMeal?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val db = Firebase.firestore

    LaunchedEffect(Unit) {
        loadFavorites { meals ->
            favoriteMeals = meals
            isLoading = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
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
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favoriteMeals) { meal ->
                        MealCard(
                            name = meal.name,
                            imageUrl = meal.imageUrl,
                            onClick = {
                                navController.navigate("detail/${meal.id}")
                            },
                            onDelete = {
                                mealToDelete = meal
                            }
                        )
                    }
                }
            }

            // 🧾 Confirm Delete Dialog
            mealToDelete?.let { meal ->
                AlertDialog(
                    onDismissRequest = { mealToDelete = null },
                    title = { Text("Remove Favorite?") },
                    text = { Text("Are you sure you want to remove ${meal.name} from your favorites?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                mealToDelete = null // clear state first
                                db.collection("favorites")
                                    .document(meal.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        favoriteMeals = favoriteMeals.filter { it.id != meal.id }
                                        scope.launch {
                                            snackbarHostState.showSnackbar("✅ Meal removed from Favorites")
                                        }
                                    }
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { mealToDelete = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

private fun loadFavorites(onLoaded: (List<FavoriteMeal>) -> Unit) {
    val db = Firebase.firestore
    db.collection("favorites")
        .get()
        .addOnSuccessListener { result ->
            val meals = result.map { document ->
                FavoriteMeal(
                    id = document.getString("id") ?: "",
                    name = document.getString("name") ?: "",
                    imageUrl = document.getString("imageUrl") ?: ""
                )
            }
            onLoaded(meals)
        }
}
