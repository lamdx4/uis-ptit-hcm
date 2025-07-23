package lamdx4.uis.ptithcm.ui.nav

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import lamdx4.uis.ptithcm.ui.theme.PTITColors

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

@Composable
fun MainNavBarScaffold(
    navController: NavHostController,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.03f)
                            )
                        )
                    )
            )

            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface,
                tonalElevation = 0.dp
            ) {
                MainNavDest.items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    NavigationBarItem(
                        icon = {
                            ModernNavIcon(
                                item = item,
                                isSelected = isSelected
                            )
                        },
                        label = {
                            ModernNavLabel(
                                text = item.label,
                                isSelected = isSelected
                            )
                        },
                        selected = isSelected,
                        onClick = { onNavigate(item.route) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Transparent, // We handle color in ModernNavIcon
                            unselectedIconColor = Color.Transparent, // We handle color in ModernNavIcon
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent // We use custom indicator
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernNavIcon(
    item: MainNavDest,
    isSelected: Boolean
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        // Background indicator
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {}
        }

        // Icon with animation
        AnimatedContent(
            targetState = isSelected,
            transitionSpec = {
                scaleIn(initialScale = 0.8f) + fadeIn() togetherWith
                        scaleOut(targetScale = 0.8f) + fadeOut()
            },
            label = "icon_animation"
        ) { selected ->
            Icon(
                imageVector = if (selected) item.selectedIcon else item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(24.dp),
                tint = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Active indicator dot
        AnimatedVisibility(
            visible = isSelected,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .offset(y = 20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
private fun ModernNavLabel(
    text: String,
    isSelected: Boolean
) {
    AnimatedContent(
        targetState = isSelected,
        transitionSpec = {
            slideInVertically(initialOffsetY = { it / 4 }) + fadeIn() togetherWith
                    slideOutVertically(targetOffsetY = { -it / 4 }) + fadeOut()
        },
        label = "label_animation"
    ) { selected ->
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Alternative Floating Action Button Style Navigation (Optional)
@Composable
fun FloatingBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.wrapContentWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainNavDest.items.forEach { item ->
                    val isSelected = currentRoute == item.route

                    FloatingNavItem(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingNavItem(
    item: MainNavDest,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .animateContentSize(),
        color = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            Color.Transparent,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (isSelected) 16.dp else 12.dp,
                vertical = 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(20.dp),
                tint = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(
                visible = isSelected,
                enter = expandHorizontally() + fadeIn(),
                exit = shrinkHorizontally() + fadeOut()
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// Enhanced Navigation with Badges (for notifications, etc.)
@Composable
fun EnhancedBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    badges: Map<String, Int> = emptyMap() // Route to badge count
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        NavigationBar(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 0.dp
        ) {
            MainNavDest.items.forEach { item ->
                val isSelected = currentRoute == item.route
                val badgeCount = badges[item.route] ?: 0

                NavigationBarItem(
                    icon = {
                        BadgedBox(
                            badge = {
                                if (badgeCount > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    ) {
                                        Text(
                                            text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            ModernNavIcon(
                                item = item,
                                isSelected = isSelected
                            )
                        }
                    },
                    label = {
                        ModernNavLabel(
                            text = item.label,
                            isSelected = isSelected
                        )
                    },
                    selected = isSelected,
                    onClick = { onNavigate(item.route) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Transparent,
                        unselectedIconColor = Color.Transparent,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
