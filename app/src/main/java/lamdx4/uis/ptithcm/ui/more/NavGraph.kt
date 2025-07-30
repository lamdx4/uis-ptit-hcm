package lamdx4.uis.ptithcm.ui.more

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import lamdx4.uis.ptithcm.ui.more.curriculum.CurriculumScreen
import lamdx4.uis.ptithcm.ui.more.feedback.FeedbackScreen
import lamdx4.uis.ptithcm.ui.more.invoices.InvoicesScreen
import lamdx4.uis.ptithcm.ui.more.notifications.NotificationsScreen
import lamdx4.uis.ptithcm.ui.more.prerequisites.PrerequisitesScreen
import lamdx4.uis.ptithcm.ui.more.sync.CalendarSyncScreen
import lamdx4.uis.ptithcm.ui.more.updateinfo.UpdateInfoScreen

fun NavGraphBuilder.moreNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    composable("more") {
        MoreScreen(navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }

    composable("detailed_info") {
        DetailedInfoScreen(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }

    // Additional More feature screens
    composable("notifications") {
        NotificationsScreen(navController = navController)
    }

    composable("curriculum") {
        CurriculumScreen(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }

    composable("prerequisites") {
        PrerequisitesScreen(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }

    composable("invoices") {
        InvoicesScreen(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }

    composable("update_info") {
        UpdateInfoScreen(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }

    composable("feedback") {
        FeedbackScreen(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }

    composable("sync_calendar") {
        CalendarSyncScreen(
            onNavigateBack = { navController.popBackStack() },
            modifier = Modifier.padding(innerPadding)
        )
    }
}