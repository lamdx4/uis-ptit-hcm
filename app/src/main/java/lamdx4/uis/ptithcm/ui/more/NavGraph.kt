package lamdx4.uis.ptithcm.ui.more

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.more.notifications.NotificationsScreen
import lamdx4.uis.ptithcm.ui.more.curriculum.CurriculumScreen
import lamdx4.uis.ptithcm.ui.more.prerequisites.PrerequisitesScreen
import lamdx4.uis.ptithcm.ui.more.invoices.InvoicesScreen
import lamdx4.uis.ptithcm.ui.more.updateinfo.UpdateInfoScreen
import lamdx4.uis.ptithcm.ui.more.feedback.FeedbackScreen
import lamdx4.uis.ptithcm.ui.sync.CalendarSyncScreen

fun NavGraphBuilder.moreNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
    appViewModel: AppViewModel
) {
    composable("more") {
        MoreScreen(navController = navController)
    }
    
    composable("detailed_info") {
        DetailedInfoScreen(navController = navController)
    }
    
    // Additional More feature screens
    composable("notifications") {
        NotificationsScreen(navController = navController)
    }
    
    composable("curriculum") {
        CurriculumScreen(navController = navController)
    }
    
    composable("prerequisites") {
        PrerequisitesScreen(navController = navController)
    }
    
    composable("invoices") {
        InvoicesScreen(navController = navController)
    }
    
    composable("update_info") {
        UpdateInfoScreen(navController = navController)
    }
    
    composable("feedback") {
        FeedbackScreen(navController = navController)
    }
    
    composable("sync_calendar") {
        CalendarSyncScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}