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
import com.example.eatright.data.api.RetrofitInstance
import com.example.eatright.data.model.Meal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessScreen(navController: NavController) {
    val goals = listOf("Weight Loss", "Muscle Gain", "Balanced Diet")
    var selectedGoal by remember { mutableStateOf<String?>(null) }
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun fetchMeals(goal: String) {
        val queries = when (goal) {
            "Weight Loss" -> listOf("salad","soup","beans","tofu","lentils")
            "Muscle Gain" -> listOf("egg","chicken","oats","fish") // ⬅️ multiple keywords
            "Balanced Diet" -> listOf( "rice","lentils","chicken","vegetables","beans")
            else -> listOf("healthy")
        }

        scope.launch {
            isLoading = true
            val allMeals = mutableListOf<Meal>()

            for (query in queries) {
                val response = RetrofitInstance.api.searchMeals(query)
                response.meals?.let {
                    allMeals.addAll(it)
                }
            }

            // Remove any duplicates (just in case)
            meals = allMeals.distinctBy { it.id }.shuffled()

            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fitness Goals") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Choose a goal",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            goals.forEach { goal ->
                ElevatedButton(
                    onClick = {
                        selectedGoal = goal
                        fetchMeals(goal)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = goal)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            selectedGoal?.let {
                Text(
                    text = "Suggested meals for \"$it\"",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(meals) { meal ->
                        MealCard(
                            name = meal.name,
                            imageUrl = meal.imageUrl,
                            onClick = {
                                navController.navigate("detail/${meal.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}


