package lamdx4.uis.ptithcm.ui.more.register

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

fun NavGraphBuilder.registerNavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    composable("register") {
        CourseRegistrationScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}