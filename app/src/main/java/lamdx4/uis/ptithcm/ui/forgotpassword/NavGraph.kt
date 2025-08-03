package lamdx4.uis.ptithcm.ui.forgotpassword


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.forgotPasswordNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    composable("forgot-password") {
        ForgotPasswordScreen(
            modifier = Modifier.padding(innerPadding),
            onPasswordResetSuccess = { navController.navigate("login") },
            onNavigateBack = { navController.popBackStack() }
        )
    }
}