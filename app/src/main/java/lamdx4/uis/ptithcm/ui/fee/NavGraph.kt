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
        route = "export_webview?url={url}&cookie={cookie}",
        arguments = listOf(
            navArgument("url") { type = NavType.StringType },
            navArgument("cookie") {
                type = NavType.StringType
                nullable = true
                defaultValue = ""
            }
        )
    ) { backStackEntry ->
        val url = backStackEntry.arguments?.getString("url") ?: ""
        val cookie = backStackEntry.arguments?.getString("cookie") ?: ""

        ExportWebScreen(
            url = url,
            cookies = cookie,
            onBack = {
                navController.popBackStack()
            }
        )
    }
}
