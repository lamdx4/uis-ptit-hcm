package lamdx4.uis.ptithcm.ui.profile

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.profileNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    accessToken: String,
    maSV: String
) {
    composable("profile") {
        ProfileScreen(
            accessToken = accessToken,
            maSV = maSV
        )
    }
}