package lamdx4.uis.ptithcm.ui.schedule

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.scheduleNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    composable("schedule") {
        ScheduleScreen()
    }
}