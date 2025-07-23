package lamdx4.uis.ptithcm.ui.grades

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import lamdx4.uis.ptithcm.data.model.SubjectGrade
import lamdx4.uis.ptithcm.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesScreen(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel(),
    gradesViewModel: GradesViewModel = viewModel()
) {
    val appState by appViewModel.uiState.collectAsState()
    val gradesState by gradesViewModel.uiState.collectAsState()

    // Dialog state for subject details
    var selectedSubject by remember { mutableStateOf<SubjectGrade?>(null) }

    // Load grades when screen is first shown
    LaunchedEffect(appState.accessToken) {
        appState.accessToken?.let { token ->
            gradesViewModel.loadGrades(token)
        }
    }

    Scaffold(
        modifier = modifier
    ) { padding ->
        when {
            gradesState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Đang tải điểm số...")
                    }
                }
            }

            gradesState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = gradesState.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    // Overall grade summary (always shown first)
                    if (gradesState.semesters.isNotEmpty()) {
                        item {
                            OverallGradeSummaryCard(gradesState.semesters)
                        }
                    }

                    // Semester selector
                    if (gradesState.semesters.isNotEmpty()) {
                        item {
                            SemesterSelector(
                                semesters = gradesState.semesters,
                                selectedSemester = gradesState.selectedSemester,
                                onSemesterSelected = { gradesViewModel.selectSemester(it) }
                            )
                        }
                    }

                    // Statistics summary for selected semester
                    gradesState.selectedSemester?.let { semester ->
                        item {
                            SemesterStatisticsCard(semester)
                        }
                    }

                    // Subject grades
                    items(gradesState.selectedSubjects) { subject ->
                        SubjectGradeCard(
                            subject = subject,
                            onClick = { selectedSubject = subject }
                        )
                    }
                }
            }
        }
    }

    // Show subject detail dialog
    selectedSubject?.let { subject ->
        SubjectDetailDialog(
            subject = subject,
            onDismiss = { selectedSubject = null }
        )
    }
}