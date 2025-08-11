package lamdx4.uis.ptithcm.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.nav.TopAppBarConfig.routeTopBarConfig
import lamdx4.uis.ptithcm.util.RefreshCoordinator
import kotlin.collections.get

sealed class MainNavDest(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
) {
    object Profile : MainNavDest(
        route = "profile",
        label = "Thông tin",
        icon = Icons.Default.Person,
        selectedIcon = Icons.Default.Person
    )

    object Schedule : MainNavDest(
        route = "schedule",
        label = "Lịch học",
        icon = Icons.Default.CalendarMonth,
        selectedIcon = Icons.Default.CalendarMonth
    )

    object Grades : MainNavDest(
        route = "grades",
        label = "Kết quả",
        icon = Icons.Default.Grade,
        selectedIcon = Icons.Default.Grade
    )

    object More : MainNavDest(
        route = "more",
        label = "Khác",
        icon = Icons.Default.MoreHoriz,
        selectedIcon = Icons.Default.MoreHoriz
    )

    companion object {
        val items = listOf(Profile, Schedule, Grades, More)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavBarScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val topBarConfig = routeTopBarConfig[currentRoute]
    val uiEventVM: RefreshCoordinator = activityViewModel<AppViewModel>().refreshCoordinator

    Scaffold(
        topBar = {
            if (topBarConfig != null) {
                androidx.compose.material3.TopAppBar(
                    title = { Text(topBarConfig.title) },
                    navigationIcon = {
                        BackButton(
                            showBack = topBarConfig.showBack,
                            action = { navController.popBackStack() }
                        )
                    },
                    actions = {
                        if (topBarConfig.showRefresh) {
                            IconButton(onClick = {
                                currentRoute?.let { uiEventVM.sendRefresh(it) }
                            }) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            ModernBottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun ModernBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    // Ultra-compact design with minimal height
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp), // Standard Material 3 bottom bar height
        shadowElevation = 3.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainNavDest.items.forEach { item ->
                val isSelected = currentRoute == item.route
                CompactNavItem(
                    item = item,
                    isSelected = isSelected,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
private fun CompactNavItem(
    item: MainNavDest,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(80.dp) // Fixed width instead of weight
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Minimal icon with simple color transition
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(22.dp), // Slightly larger for better touch target
            tint = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            }
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Minimal text label
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp, // Very small text
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Simple bottom indicator line
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(2.dp)
                .background(
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                    shape = RoundedCornerShape(1.dp)
                )
        )
    }
}

@Composable
fun BackButton(showBack: Boolean, action: () -> Unit) {
    if (showBack) {
        IconButton(onClick = { action.invoke() }) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
    }
}