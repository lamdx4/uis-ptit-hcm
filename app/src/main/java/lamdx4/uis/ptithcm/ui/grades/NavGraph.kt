package lamdx4.uis.ptithcm.ui.grades

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.gradesNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    composable("grades") {
        GradesScreen()
    }
}