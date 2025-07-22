package lamdx4.uis.ptithcm.ui.exam

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.examNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    composable("exam") {
        ExamScreen()
    }
}