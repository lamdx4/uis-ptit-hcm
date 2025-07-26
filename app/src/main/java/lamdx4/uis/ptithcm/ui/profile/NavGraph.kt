package lamdx4.uis.ptithcm.ui.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import lamdx4.uis.ptithcm.ui.AppViewModel

fun NavGraphBuilder.profileNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    composable("profile") {
        ProfileScreen(
            appViewModel = hiltViewModel(),
            modifier = Modifier.padding(innerPadding)
        )
    }
}