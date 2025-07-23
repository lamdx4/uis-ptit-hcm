package lamdx4.uis.ptithcm.ui.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import lamdx4.uis.ptithcm.ui.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyScheduleScreen(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel(),
    weeklyScheduleViewModel: WeeklyScheduleViewModel = viewModel()
) {
    val appState by appViewModel.uiState.collectAsState()
    val scheduleState by weeklyScheduleViewModel.uiState.collectAsState()

    // Load schedule when screen is first shown
    LaunchedEffect(appState.accessToken) {
        appState.accessToken?.let { token ->
            weeklyScheduleViewModel.loadSchedule(token)
        }
    }

    // TODO: Remove this when API is implemented - Sample data for testing
    LaunchedEffect(Unit) {
        // Sample data based on your API structure
        val sampleWeeks = listOf(
            lamdx4.uis.ptithcm.data.model.WeeklySchedule(
                semesterWeek = 14,
                absoluteWeek = 1246,
                weekInfo = "Tuần 14 [từ ngày 13/11/2023 đến ngày 19/11/2023]",
                startDate = "13/11/2023",
                endDate = "19/11/2023",
                scheduleItems = listOf(
                    lamdx4.uis.ptithcm.data.model.ScheduleItem(
                        dayOfWeek = 3, // Tuesday
                        startPeriod = 1,
                        numberOfPeriods = 4,
                        subjectCode = "SKD1101",
                        subjectName = "Kỹ năng thuyết trình",
                        credits = "1",
                        groupCode = "03",
                        teacherName = "Hoàng Hà Linh",
                        roomCode = "2A2425-2A2425"
                    ),
                    lamdx4.uis.ptithcm.data.model.ScheduleItem(
                        dayOfWeek = 3, // Tuesday
                        startPeriod = 7,
                        numberOfPeriods = 4,
                        subjectCode = "INT1339",
                        subjectName = "Ngôn ngữ lập trình C++",
                        credits = "3",
                        groupCode = "02",
                        teacherName = "Phan Nghĩa Hiệp",
                        roomCode = "2B11-2B11 (Q9)"
                    ),
                    lamdx4.uis.ptithcm.data.model.ScheduleItem(
                        dayOfWeek = 4, // Wednesday
                        startPeriod = 1,
                        numberOfPeriods = 4,
                        subjectCode = "BAS1158",
                        subjectName = "Tiếng Anh (Course 2)",
                        credits = "4",
                        groupCode = "03",
                        teacherName = "Nguyễn Đại Phong",
                        roomCode = "2E17-Ngoai ngu"
                    ),
                    lamdx4.uis.ptithcm.data.model.ScheduleItem(
                        dayOfWeek = 4, // Wednesday
                        startPeriod = 7,
                        numberOfPeriods = 4,
                        subjectCode = "BAS1152",
                        subjectName = "Chủ nghĩa xã hội khoa học",
                        credits = "2",
                        groupCode = "03",
                        teacherName = "Nguyễn Xuân Lưu",
                        roomCode = "2A16-2A16"
                    ),
                    lamdx4.uis.ptithcm.data.model.ScheduleItem(
                        dayOfWeek = 6, // Friday
                        startPeriod = 7,
                        numberOfPeriods = 4,
                        subjectCode = "BAS1158",
                        subjectName = "Tiếng Anh (Course 2)",
                        credits = "4",
                        groupCode = "03",
                        teacherName = "Dương Trần Thuỷ Trinh",
                        roomCode = "2E14-Ngoai ngu"
                    )
                ),
                conflictingScheduleIds = emptyList()
            ),
            lamdx4.uis.ptithcm.data.model.WeeklySchedule(
                semesterWeek = 15,
                absoluteWeek = 1247,
                weekInfo = "Tuần 15 [từ ngày 20/11/2023 đến ngày 26/11/2023]",
                startDate = "20/11/2023",
                endDate = "26/11/2023",
                scheduleItems = listOf(
                    lamdx4.uis.ptithcm.data.model.ScheduleItem(
                        dayOfWeek = 3, // Tuesday
                        startPeriod = 1,
                        numberOfPeriods = 4,
                        subjectCode = "SKD1101",
                        subjectName = "Kỹ năng thuyết trình",
                        credits = "1",
                        groupCode = "03",
                        teacherName = "Hoàng Hà Linh",
                        roomCode = "2A2425-2A2425"
                    ),
                    lamdx4.uis.ptithcm.data.model.ScheduleItem(
                        dayOfWeek = 3, // Tuesday
                        startPeriod = 7,
                        numberOfPeriods = 4,
                        subjectCode = "INT1339",
                        subjectName = "Ngôn ngữ lập trình C++",
                        credits = "3",
                        groupCode = "02",
                        teacherName = "Phan Nghĩa Hiệp",
                        roomCode = "2B11-2B11 (Q9)"
                    )
                ),
                conflictingScheduleIds = emptyList()
            )
        )
        weeklyScheduleViewModel.setWeeklySchedules(sampleWeeks)
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        when {
            scheduleState.isLoading -> {
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
                    // Week selector
                    WeekSelector(
                        weeks = scheduleState.weeklySchedules,
                        selectedWeek = scheduleState.selectedWeek,
                        onWeekSelected = { weeklyScheduleViewModel.selectWeek(it) }
                    )
                    
                    // Current week info and schedule
                    scheduleState.currentWeekDisplay?.let { weekDisplay ->
                        WeekInfoCard(
                            weekInfo = weekDisplay.weekInfo,
                            startDate = weekDisplay.startDate,
                            endDate = weekDisplay.endDate
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
            }
            
            Text(
                text = "Từ $startDate đến $endDate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
