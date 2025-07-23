package lamdx4.uis.ptithcm.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.WeeklySchedule
import lamdx4.uis.ptithcm.data.model.WeekScheduleDisplay
import lamdx4.uis.ptithcm.data.model.DaySchedule
import lamdx4.uis.ptithcm.data.model.ScheduleItem
import lamdx4.uis.ptithcm.data.model.Semester
import java.text.SimpleDateFormat
import java.util.*

data class WeeklyScheduleUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val semesters: List<Semester> = emptyList(),
    val selectedSemester: Semester? = null,
    val weeklySchedules: List<WeeklySchedule> = emptyList(),
    val selectedWeek: WeeklySchedule? = null,
    val currentWeekDisplay: WeekScheduleDisplay? = null,
    val isLoadingSemesters: Boolean = false,
    val isLoadingSchedule: Boolean = false
)

class WeeklyScheduleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WeeklyScheduleUiState())
    val uiState: StateFlow<WeeklyScheduleUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun loadSemesters(accessToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSemesters = true, error = null)
            
            try {
                // TODO: Implement API call to fetch semesters
                // Call w-locdshockytkbuser API
                // val response = apiService.getSemesters(accessToken)
                
                // For now, use sample data
                val sampleSemesters = listOf(
                    Semester(20233, "Học kỳ 3 Năm học 2023-2024", 0, "01/04/2024", "05/08/2024"),
                    Semester(20232, "Học kỳ 2 Năm học 2023-2024", 0, "04/12/2023", "03/06/2024"),
                    Semester(20231, "Học kỳ 1 Năm học 2023-2024", 0, "14/08/2023", "18/03/2024"),
                    Semester(20223, "Học kỳ 3 Năm học 2022-2023", 0, "03/04/2023", "14/08/2023"),
                    Semester(20222, "Học kỳ 2 Năm học 2022-2023", 0, "02/01/2023", "26/06/2023"),
                    Semester(20221, "Học kỳ 1 Năm học 2022-2023", 0, "15/08/2022", "02/01/2023")
                )
                
                _uiState.value = _uiState.value.copy(
                    isLoadingSemesters = false,
                    semesters = sampleSemesters,
                    selectedSemester = sampleSemesters.firstOrNull() // Default to first semester (most recent)
                )
                
                // Auto-load schedule for default semester
                sampleSemesters.firstOrNull()?.let { loadScheduleForSemester(accessToken, it) }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingSemesters = false,
                    error = "Lỗi khi tải danh sách học kỳ: ${e.message}"
                )
            }
        }
    }

    fun selectSemester(semester: Semester, accessToken: String) {
        _uiState.value = _uiState.value.copy(selectedSemester = semester)
        loadScheduleForSemester(accessToken, semester)
    }

    private fun loadScheduleForSemester(accessToken: String, semester: Semester) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSchedule = true, error = null)
            
            try {
                // TODO: Implement API call to fetch schedule for specific semester
                // Call w-locdstkbtuanusertheohocky API with semester.semesterCode
                // val response = apiService.getWeeklySchedule(accessToken, semester.semesterCode)
                
                // For now, use sample data based on the API structure
                val sampleSchedules = createSampleSchedules(semester.semesterCode)
                
                _uiState.value = _uiState.value.copy(
                    isLoadingSchedule = false,
                    weeklySchedules = sampleSchedules,
                    selectedWeek = sampleSchedules.firstOrNull(),
                    currentWeekDisplay = sampleSchedules.firstOrNull()?.let { createWeekDisplay(it) }
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingSchedule = false,
                    error = "Lỗi khi tải thời khóa biểu: ${e.message}"
                )
            }
        }
    }

    fun selectWeek(week: WeeklySchedule) {
        _uiState.value = _uiState.value.copy(
            selectedWeek = week,
            currentWeekDisplay = createWeekDisplay(week)
        )
    }

    private fun createWeekDisplay(week: WeeklySchedule): WeekScheduleDisplay {
        val daySchedules = mutableMapOf<Int, MutableList<ScheduleItem>>()
        
        // Group schedule items by day of week
        week.scheduleItems.forEach { item ->
            item.dayOfWeek?.let { day ->
                daySchedules.getOrPut(day) { mutableListOf() }.add(item)
            }
        }
        
        // Create day schedules for the entire week (Monday to Sunday)
        val weekDaySchedules = (2..8).map { dayOfWeek ->
            val dayName = getDayName(dayOfWeek)
            val date = getDateForDay(week.startDate, dayOfWeek)
            val items = daySchedules[dayOfWeek]?.sortedBy { it.startPeriod } ?: emptyList()
            
            DaySchedule(
                dayOfWeek = dayOfWeek,
                dayName = dayName,
                date = date,
                scheduleItems = items
            )
        }
        
        return WeekScheduleDisplay(
            weekInfo = week.weekInfo ?: "Tuần học",
            startDate = week.startDate ?: "",
            endDate = week.endDate ?: "",
            daySchedules = weekDaySchedules
        )
    }
    
    private fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            2 -> "Thứ 2"
            3 -> "Thứ 3"
            4 -> "Thứ 4"
            5 -> "Thứ 5" 
            6 -> "Thứ 6"
            7 -> "Thứ 7"
            8 -> "Chủ nhật"
            else -> "Không xác định"
        }
    }
    
    private fun getDateForDay(startDateStr: String?, dayOfWeek: Int): String {
        if (startDateStr.isNullOrEmpty()) return ""
        
        try {
            val startDate = dateFormat.parse(startDateStr) ?: return ""
            val calendar = Calendar.getInstance()
            calendar.time = startDate
            
            // Add days to get to the specific day of week
            // dayOfWeek: 2=Monday, 3=Tuesday, etc.
            val daysToAdd = dayOfWeek - 2 // Monday is day 0 in our calculation
            calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)
            
            return dateFormat.format(calendar.time)
        } catch (e: Exception) {
            return ""
        }
    }
    
    private fun createSampleSchedules(semesterCode: Int): List<WeeklySchedule> {
        // Create sample schedules based on the API structure for different semesters
        return listOf(
            WeeklySchedule(
                semesterWeek = 1,
                absoluteWeek = 1233,
                weekInfo = "Tuần 1 [từ ngày 14/08/2023 đến ngày 20/08/2023]",
                startDate = "14/08/2023",
                endDate = "20/08/2023",
                scheduleItems = listOf(
                    ScheduleItem(
                        dayOfWeek = 2, // Monday
                        startPeriod = 1,
                        numberOfPeriods = 4,
                        subjectCode = "BAS1227",
                        subjectName = "Vật lý 3 và thí nghiệm",
                        credits = "4",
                        groupCode = "02",
                        teacherCode = "TG136",
                        teacherName = "Mã Thuý Quang",
                        classCode = "D22CQCN01-N",
                        roomCode = "2A16-2A16",
                        campusCode = "MN",
                        studyDate = "2023-08-14T00:00:00"
                    ),
                    ScheduleItem(
                        dayOfWeek = 2, // Monday
                        startPeriod = 7,
                        numberOfPeriods = 4,
                        subjectCode = "ELE1330",
                        subjectName = "Xử lý tín hiệu số",
                        credits = "2",
                        groupCode = "02",
                        teacherCode = "0221049",
                        teacherName = "Nguyễn Lương Nhật",
                        classCode = "D22CQCN01-N",
                        roomCode = "2A16-2A16",
                        campusCode = "MN",
                        studyDate = "2023-08-14T00:00:00"
                    ),
                    ScheduleItem(
                        dayOfWeek = 5, // Friday
                        startPeriod = 1,
                        numberOfPeriods = 4,
                        subjectCode = "INT1339",
                        subjectName = "Ngôn ngữ lập trình C++",
                        credits = "3",
                        groupCode = "02",
                        teacherCode = "GV/N-20238",
                        teacherName = "Phan Nghĩa Hiệp",
                        classCode = "D22CQCN01-N",
                        roomCode = "2B33-2B33",
                        campusCode = "MN",
                        studyDate = "2023-08-17T00:00:00"
                    ),
                    ScheduleItem(
                        dayOfWeek = 5, // Friday
                        startPeriod = 7,
                        numberOfPeriods = 4,
                        subjectCode = "INT1358",
                        subjectName = "Toán rời rạc 1",
                        credits = "3",
                        groupCode = "02",
                        teacherCode = "0211021",
                        teacherName = "Huỳnh Trọng Thưa",
                        classCode = "D22CQCN01-N",
                        roomCode = "2B25-2B25",
                        campusCode = "MN",
                        studyDate = "2023-08-17T00:00:00"
                    )
                ),
                conflictingScheduleIds = emptyList()
            ),
            WeeklySchedule(
                semesterWeek = 2,
                absoluteWeek = 1234,
                weekInfo = "Tuần 2 [từ ngày 21/08/2023 đến ngày 27/08/2023]",
                startDate = "21/08/2023",
                endDate = "27/08/2023",
                scheduleItems = listOf(
                    // Similar schedule items for week 2
                    ScheduleItem(
                        dayOfWeek = 2,
                        startPeriod = 1,
                        numberOfPeriods = 4,
                        subjectCode = "BAS1227",
                        subjectName = "Vật lý 3 và thí nghiệm",
                        credits = "4",
                        groupCode = "02",
                        teacherCode = "TG136",
                        teacherName = "Mã Thuý Quang",
                        classCode = "D22CQCN01-N",
                        roomCode = "2A16-2A16",
                        campusCode = "MN",
                        studyDate = "2023-08-21T00:00:00"
                    )
                ),
                conflictingScheduleIds = emptyList()
            )
        )
    }
    
    fun getCurrentWeek(): WeeklySchedule? {
        val currentDate = Date()
        return _uiState.value.weeklySchedules.find { week ->
            try {
                val startDate = week.startDate?.let { dateFormat.parse(it) }
                val endDate = week.endDate?.let { dateFormat.parse(it) }
                
                startDate != null && endDate != null &&
                currentDate.after(startDate) && currentDate.before(endDate)
            } catch (e: Exception) {
                false
            }
        }
    }
    
    fun setWeeklySchedules(schedules: List<WeeklySchedule>) {
        _uiState.value = _uiState.value.copy(
            weeklySchedules = schedules.filter { it.scheduleItems.isNotEmpty() },
            isLoading = false
        )
        
        // Auto-select current week if available
        getCurrentWeek()?.let { currentWeek ->
            selectWeek(currentWeek)
        } ?: run {
            // If no current week, select first available week
            schedules.firstOrNull { it.scheduleItems.isNotEmpty() }?.let { firstWeek ->
                selectWeek(firstWeek)  
            }
        }
    }
}
