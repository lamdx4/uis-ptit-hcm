package lamdx4.uis.ptithcm.ui.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    composable("profile") {
        ProfileScreen(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}