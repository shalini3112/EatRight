package com.example.eatright.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eatright.ui.screens.*

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail/{mealId}"
    const val FAVORITES = "favorites"
    const val FITNESS = "fitness"
    const val SPLASH = "splash"


}

@Composable
fun AppNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.HOME) {
            HomeScreen(navController)
        }
        composable(Routes.DETAIL) { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
            MealDetailScreen(mealId, navController)
        }
        composable(Routes.FAVORITES) {
            FavoritesScreen(navController)
        }
        composable(Routes.FITNESS) {
            FitnessScreen(navController)
        }
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }


    }
}
