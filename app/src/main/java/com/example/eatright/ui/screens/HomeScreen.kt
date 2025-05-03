package com.example.eatright.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.eatright.data.api.RetrofitInstance
import com.example.eatright.data.model.Meal
import kotlinx.coroutines.launch
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.draw.clip


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val categories = listOf("Breakfast", "Dessert", "Vegetarian", "Vegan")
    val dietaryGoals = listOf("Keto", "Low Carb", "High Protein")
    var selectedCategory by remember { mutableStateOf("Breakfast") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGoal by remember { mutableStateOf<String?>(null) }


    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(selectedCategory, searchQuery) {
        scope.launch {
            try {
                isLoading = true
                isError = false

                val queries = when (searchQuery.lowercase()) {
                    "keto" -> listOf("chicken","fish","eggs","cheese","berries","nuts","seeds","Avacados")
                    "low carb" -> listOf("zucchini","Avacado","eggs","mushroom","brocoli","nuts")
                    "high protein" -> listOf("steak","ham","bacon","chicken","tuna","salmon","trout","crab","eggs")
                    "vegan"->listOf("vegan")
                    else -> listOf(searchQuery)
                }

                meals = if (searchQuery.isNotBlank()) {
                    val allMeals = mutableListOf<Meal>()
                    for (query in queries) {
                        val response = RetrofitInstance.api.searchMeals(query)
                        response.meals?.let { allMeals.addAll(it) }
                    }
                    allMeals.distinctBy { it.id }.shuffled()

                } else if (selectedCategory in listOf("Keto", "Gluten Free", "Dairy Free")) {
                    emptyList()
                } else {
                    val response = RetrofitInstance.api.getMealsByCategory(selectedCategory)
                    response.meals ?: emptyList()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                isError = true
            } finally {
                isLoading = false
            }
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EatRight") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("fitness") }) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = "Fitness Goals"
                        )
                    }
                    IconButton(onClick = { navController.navigate("favorites") }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites"
                        )
                    }
                }

            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 🔍 Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Meals") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )


            // 🍽️ Category Filter Row
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(initialOffsetX = { -300 }) + fadeIn()
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories.size) { index ->
                        FilterChip(
                            category = categories[index],
                            isSelected = selectedCategory == categories[index],
                            onSelected = {
                                selectedCategory = categories[index]
                                searchQuery = "" // reset search
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dietaryGoals) { goal ->
                    AssistChip(
                        onClick = {
                            selectedGoal = if (selectedGoal == goal) null else goal
                            searchQuery = goal // trigger meal search
                        },
                        label = {
                            Text(
                                text = goal,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (selectedGoal == goal)
                                MaterialTheme.colorScheme.primaryContainer
                            else
                                MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = if (selectedGoal == goal)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }


            // ✨ Dynamic Title
            Text(
                text = if (searchQuery.isNotBlank()) "Search Results for \"$searchQuery\"" else "Showing: $selectedCategory Meals",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Content Section
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    searchQuery = searchQuery // to trigger LaunchedEffect again
                }
            ) {
                Crossfade(targetState = isLoading to meals) { (loading, currentMeals) ->
                    when {
                        loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        isError -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "Failed to load meals. Check internet!")
                            }
                        }
                        currentMeals.isEmpty() -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "No meals found 🥲")
                            }
                        }
                        else -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(currentMeals) { meal ->
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
        }
    }
}

@Composable
fun FilterChip(category: String, isSelected: Boolean, onSelected: () -> Unit) {
    AssistChip(
        onClick = onSelected,
        label = {
            Text(
                text = category,
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant,
            labelColor = if (isSelected)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        ),

        shape = RoundedCornerShape(20.dp),
        border = null,
        elevation = AssistChipDefaults.assistChipElevation(4.dp) // ✅ Fixed here
    )
}


@Composable
fun MealItem(meal: Meal, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(meal.imageUrl),
                contentDescription = meal.name,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = meal.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
        }
    }
}

