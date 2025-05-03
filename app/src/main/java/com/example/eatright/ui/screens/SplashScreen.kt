package com.example.eatright.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eatright.ui.navigation.Routes
import com.example.eatright.ui.theme.Lobster
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var displayedText by remember { mutableStateOf("") }
    val fullText = "EatRight"

    LaunchedEffect(Unit) {
        // Reveal one letter at a time
        for (i in fullText.indices) {
            displayedText = fullText.substring(0, i + 1)
            delay(450) // 150ms between letters
        }
        delay(1000) // Hold full word before navigating
        navController.navigate(Routes.HOME) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C2135)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayedText,
            fontSize = 48.sp,
            fontFamily = Lobster,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD9D6F9)
        )
    }
}
