package lamdx4.uis.ptithcm.ui.more.prerequisites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrerequisitesScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    viewModel: PrerequisitesViewModel = hiltViewModel(),
    appViewModel: AppViewModel = activityViewModel<AppViewModel>()
) {
    val prerequisitesResponse by viewModel.prerequisites.collectAsState()
    val prerequisitesTypeResponse by viewModel.prerequisiteType.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val refreshCoordinator = appViewModel.refreshCoordinator

    var selectedType by remember(prerequisitesTypeResponse) {
        mutableStateOf(prerequisitesTypeResponse.firstOrNull())
    }

    LaunchedEffect(Unit) {
        refreshCoordinator.refreshEvent.collect { route ->
            if (route == "prerequisites") {
                viewModel.refreshPrerequisites()
                viewModel.refreshPrerequisiteTypes()
            }
        }
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    errorMessage != null -> {
                        ErrorView(message = errorMessage!!)
                    }

                    else -> {
                        val listData = prerequisitesResponse?.data?.prerequisites ?: emptyList()
                        PrerequisiteTable(
                            data = listData,
                            typeList = prerequisitesTypeResponse,
                            selectedType = selectedType,
                            onTypeSelected = { newType ->
                                selectedType = newType
                                viewModel.refreshPrerequisites(newType.value)
                            },
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
