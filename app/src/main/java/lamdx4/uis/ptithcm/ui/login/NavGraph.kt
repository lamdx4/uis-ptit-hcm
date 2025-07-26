package lamdx4.uis.ptithcm.ui.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import lamdx4.uis.ptithcm.ui.AppViewModel

fun NavGraphBuilder.loginNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues(),
) {
    composable("login") {
        LoginScreen(
            innerPadding = innerPadding,
            onLoginSuccess = {
                navController.navigate("profile") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }
}