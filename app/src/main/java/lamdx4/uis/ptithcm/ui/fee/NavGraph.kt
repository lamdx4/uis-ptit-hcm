package lamdx4.uis.ptithcm.ui.fee

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import lamdx4.uis.ptithcm.ui.AppViewModel

fun NavGraphBuilder.feeNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    appViewModel: AppViewModel
) {
    composable("fee") {
        FeeScreen()
    }
}