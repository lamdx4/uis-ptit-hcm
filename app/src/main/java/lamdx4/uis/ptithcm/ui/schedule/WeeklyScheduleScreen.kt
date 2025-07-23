package lamdx4.uis.ptithcm.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.data.model.Semester

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyScheduleScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController? = null,
    appViewModel: AppViewModel = viewModel(),
    weeklyScheduleViewModel: WeeklyScheduleViewModel = viewModel()
) {
    val appState by appViewModel.uiState.collectAsState()
    val scheduleState by weeklyScheduleViewModel.uiState.collectAsState()

    // Load semesters when screen is first shown
    LaunchedEffect(appState.accessToken) {
        appState.accessToken?.let { token ->
            weeklyScheduleViewModel.loadSemesters(token)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null
                        )
                        Text("Thời khóa biểu tuần")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        when {
            scheduleState.isLoadingSemesters || scheduleState.isLoadingSchedule -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Đang tải thời khóa biểu...")
                    }
                }
            }
            
            scheduleState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = scheduleState.error ?: "Lỗi không xác định",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            
            scheduleState.weeklySchedules.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Không có thời khóa biểu",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Semester selector
                    SemesterSelector(
                        semesters = scheduleState.semesters,
                        selectedSemester = scheduleState.selectedSemester,
                        onSemesterSelected = { semester ->
                            appState.accessToken?.let { token ->
                                weeklyScheduleViewModel.selectSemester(semester, token)
                            }
                        }
                    )
                    
                    // Week selector
                    WeekSelector(
                        weeks = scheduleState.weeklySchedules,
                        selectedWeek = scheduleState.selectedWeek,
                        currentWeek = scheduleState.currentWeek,
                        onWeekSelected = { weeklyScheduleViewModel.selectWeek(it) }
                    )
                    
                    // Current week info and schedule
                    scheduleState.currentWeekDisplay?.let { weekDisplay ->
                        val isCurrentWeek = scheduleState.currentWeek?.absoluteWeek == scheduleState.selectedWeek?.absoluteWeek
                        WeekInfoCard(
                            weekInfo = weekDisplay.weekInfo,
                            startDate = weekDisplay.startDate,
                            endDate = weekDisplay.endDate,
                            isCurrentWeek = isCurrentWeek
                        )
                        
                        // Weekly schedule
                        WeeklyScheduleView(
                            daySchedules = weekDisplay.daySchedules,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeekInfoCard(
    weekInfo: String,
    startDate: String,
    endDate: String,
    isCurrentWeek: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = weekInfo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                if (isCurrentWeek) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Tuần hiện tại",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Text(
                text = "Từ $startDate đến $endDate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SemesterSelector(
    semesters: List<Semester>,
    selectedSemester: Semester?,
    onSemesterSelected: (Semester) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Chọn học kỳ:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedSemester?.displayName ?: "Chọn học kỳ",
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    semesters.forEach { semester ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = semester.displayName,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${semester.startDate} - ${semester.endDate}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                onSemesterSelected(semester)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
