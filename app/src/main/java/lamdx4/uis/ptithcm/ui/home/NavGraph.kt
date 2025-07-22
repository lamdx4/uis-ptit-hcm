package lamdx4.uis.ptithcm.ui.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.homeScreen(navController: NavHostController) {
    composable("home") {
        HomeScreen(navController)
    }
}