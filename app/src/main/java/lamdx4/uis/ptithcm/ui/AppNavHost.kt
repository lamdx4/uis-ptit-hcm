package lamdx4.uis.ptithcm.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
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
    appViewModel: AppViewModel
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
                loginNavGraph(navController, innerPadding)
                profileNavGraph(navController, innerPadding)
                scheduleNavGraph(navController, innerPadding)
                gradesNavGraph(navController, innerPadding)
                examNavGraph(navController, innerPadding)
                registerNavGraph(navController, innerPadding)
                feeNavGraph(navController, innerPadding)
                moreNavGraph(navController, innerPadding)
            }
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            loginNavGraph(navController, PaddingValues())
            profileNavGraph(navController, PaddingValues())
            scheduleNavGraph(navController, PaddingValues())
            gradesNavGraph(navController, PaddingValues())
            examNavGraph(navController, PaddingValues())
            registerNavGraph(navController, PaddingValues())
            feeNavGraph(navController, PaddingValues())
            moreNavGraph(navController, PaddingValues())
        }
    }
}