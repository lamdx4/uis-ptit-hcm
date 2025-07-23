package lamdx4.uis.ptithcm.ui.schedule

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import lamdx4.uis.ptithcm.ui.AppViewModel

fun NavGraphBuilder.scheduleNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    appViewModel: AppViewModel
) {
    composable("schedule") {
        ScheduleScreen(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
    
    composable("weekly_schedule") {
        WeeklyScheduleScreen(
            modifier = Modifier.padding(innerPadding),
            navController = navController
        )
    }
}