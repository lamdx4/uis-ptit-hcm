package lamdx4.uis.ptithcm.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class MainNavDest(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Profile : MainNavDest("profile", "Thông tin", Icons.Default.Person)
    object Schedule : MainNavDest("schedule", "Thời khoá biểu", Icons.Default.CalendarMonth)
    object Grades : MainNavDest("grades", "Kết quả", Icons.Default.Star)
    object Exam : MainNavDest("exam", "Lịch thi", Icons.Default.Event)
    object Register : MainNavDest("register", "Đăng ký", Icons.Default.Edit)
    object More : MainNavDest("more", "More", Icons.Default.MoreHoriz)

    companion object {
        val items = listOf(Profile, Schedule, Grades, Exam, Register, More)
    }
}

@Composable
fun MainNavBarScaffold(
    navController: NavHostController,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                MainNavDest.items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}