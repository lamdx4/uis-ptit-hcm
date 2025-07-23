package lamdx4.uis.ptithcm.ui.more

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import lamdx4.uis.ptithcm.ui.AppViewModel

fun NavGraphBuilder.moreNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    appViewModel: AppViewModel
) {
    composable("more") {
        MoreScreen(navController = navController)
    }
}