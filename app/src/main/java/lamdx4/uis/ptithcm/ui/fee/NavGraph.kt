package lamdx4.uis.ptithcm.ui.fee

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.feeNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    composable("fee") {
        FeeScreen(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }

    composable(
        route = "export_webview?url={url}",
        arguments = listOf(
            navArgument("url") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        // Lấy URL từ navigation (đã tự động decode)
        val url = backStackEntry.arguments?.getString("url") ?: "https://uis.ptithcm.edu.vn/"

        // Gọi màn hình ExportWebScreen
        ExportWebScreen(
            url = url,
            onBack = {
                navController.popBackStack()
            }
        )
    }
}
