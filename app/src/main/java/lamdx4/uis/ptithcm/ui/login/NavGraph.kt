package lamdx4.uis.ptithcm.ui.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.loginNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues? = null // optional, nếu cần
) {
    composable("login") {
        LoginScreen(
            onLoginSuccess = {
                navController.navigate("profile") { // hoặc "schedule", tuỳ app bạn muốn
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }
}