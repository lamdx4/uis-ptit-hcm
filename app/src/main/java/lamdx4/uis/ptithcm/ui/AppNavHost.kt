package lamdx4.uis.ptithcm.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import lamdx4.uis.ptithcm.ui.login.loginNavGraph
import lamdx4.uis.ptithcm.ui.nav.MainNavBarScaffold
import lamdx4.uis.ptithcm.ui.profile.profileNavGraph
import lamdx4.uis.ptithcm.ui.schedule.scheduleNavGraph
import lamdx4.uis.ptithcm.ui.grades.gradesNavGraph
import lamdx4.uis.ptithcm.ui.exam.examNavGraph
import lamdx4.uis.ptithcm.ui.register.registerNavGraph
import lamdx4.uis.ptithcm.ui.fee.feeNavGraph
import lamdx4.uis.ptithcm.ui.more.moreNavGraph

@Composable
fun AppNavHost(
    appViewModel: AppViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomNav = currentRoute != "login"

    if (showBottomNav) {
        MainNavBarScaffold(navController) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                loginNavGraph(navController,innerPadding, appViewModel)
                profileNavGraph(navController, innerPadding, appViewModel)
                scheduleNavGraph(navController, innerPadding, appViewModel)
                gradesNavGraph(navController, innerPadding, appViewModel)
                examNavGraph(navController, innerPadding, appViewModel)
                registerNavGraph(navController, innerPadding, appViewModel)
                feeNavGraph(navController, innerPadding, appViewModel)
                moreNavGraph(navController, innerPadding, appViewModel)
            }
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            loginNavGraph(navController, PaddingValues(), appViewModel)
            profileNavGraph(navController, PaddingValues(), appViewModel)
            scheduleNavGraph(navController, PaddingValues(), appViewModel)
            gradesNavGraph(navController, PaddingValues(), appViewModel)
            examNavGraph(navController, PaddingValues(), appViewModel)
            registerNavGraph(navController, PaddingValues(), appViewModel)
            feeNavGraph(navController, PaddingValues(), appViewModel)
            moreNavGraph(navController, PaddingValues(), appViewModel)
        }
    }
}