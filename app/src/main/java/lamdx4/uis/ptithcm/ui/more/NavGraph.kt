package lamdx4.uis.ptithcm.ui.more

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.moreNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    composable("more") {
        MoreScreen()
    }
}