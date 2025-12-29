package lamdx4.uis.ptithcm.ui.fee

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.feeNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    composable("fee") {
        FeeScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}