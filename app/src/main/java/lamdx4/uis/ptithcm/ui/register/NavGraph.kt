package lamdx4.uis.ptithcm.ui.register

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import lamdx4.uis.ptithcm.ui.AppViewModel

fun NavGraphBuilder.registerNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    composable("register") {
        RegisterScreen()
    }
}