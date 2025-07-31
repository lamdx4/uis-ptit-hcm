package lamdx4.uis.ptithcm.ui.nav

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController

// Extension functions for navigation
@Composable
fun NavHostController.getCurrentRoute(): String? {
    return this.currentBackStackEntry?.destination?.route
}

// Custom navigation animations
object NavigationAnimations {
    val slideInFromRight = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth }
    ) + fadeIn()

    val slideOutToLeft = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth }
    ) + fadeOut()

    val slideInFromLeft = slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth }
    ) + fadeIn()

    val slideOutToRight = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth }
    ) + fadeOut()

    val fadeInOut = fadeIn() togetherWith fadeOut()

    val scaleInOut = scaleIn(initialScale = 0.9f) + fadeIn() togetherWith
            scaleOut(targetScale = 1.1f) + fadeOut()
}

// Navigation state management
@Composable
fun rememberNavigationState(
    navController: NavHostController
): NavigationState {
    return remember(navController) {
        NavigationState(navController)
    }
}

class NavigationState(
    private val navController: NavHostController
) {
    val currentRoute: String?
        @Composable get() = navController.getCurrentRoute()

    @Composable
    fun NavigateTo(route: String) {
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

    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateToWithClearStack(route: String) {
        navController.navigate(route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
}

