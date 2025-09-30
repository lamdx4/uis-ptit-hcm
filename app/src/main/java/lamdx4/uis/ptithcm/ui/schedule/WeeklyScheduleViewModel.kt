package lamdx4.uis.ptithcm.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.DaySchedule
import lamdx4.uis.ptithcm.data.model.ScheduleItem
import lamdx4.uis.ptithcm.data.model.Semester
import lamdx4.uis.ptithcm.data.model.WeekScheduleDisplay
import lamdx4.uis.ptithcm.data.model.WeeklySchedule
import lamdx4.uis.ptithcm.data.repository.ScheduleRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class WeeklyScheduleUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val semesters: List<Semester> = emptyList(),
    val selectedSemester: Semester? = null,
    val weeklySchedules: List<WeeklySchedule> = emptyList(),
    val selectedWeek: WeeklySchedule? = null,
    val currentWeek: WeeklySchedule? = null, // Tuần hiện tại dựa trên ngày
    val currentWeekDisplay: WeekScheduleDisplay? = null,
    val isLoadingSemesters: Boolean = false,
    val isLoadingSchedule: Boolean = false
)

@HiltViewModel
class WeeklyScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(WeeklyScheduleUiState())
    val uiState: StateFlow<WeeklyScheduleUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun loadSemesters() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSemesters = true, error = null)
            
            try {
                val semesterResponse = scheduleRepository.getSemesters()
                val semesters = semesterResponse.data.semesters
                
                // Lấy học kỳ hiện tại hoặc mặc định học kỳ đầu tiên
                val currentSemester = scheduleRepository.getCurrentSemester()
                    ?: semesters.firstOrNull()
                
                _uiState.value = _uiState.value.copy(
                    isLoadingSemesters = false,
                    semesters = semesters,
                    selectedSemester = currentSemester
                )
                
                // Auto-load schedule for default semester
                currentSemester?.let { loadScheduleForSemester(it) }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingSemesters = false,
                    error = "Lỗi khi tải danh sách học kỳ: ${e.message}"
                )
            }
        }
    }

    fun selectSemester(semester: Semester) {
        _uiState.value = _uiState.value.copy(
            selectedSemester = semester,
            isLoadingSchedule = true,
            error = null
        )
        loadScheduleForSemester(semester)
    }

    private fun loadScheduleForSemester(semester: Semester) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSchedule = true, error = null)
            
            try {
                val scheduleResponse = scheduleRepository.getWeeklySchedule( semester.semesterCode)
                // Hiển thị tất cả tuần, kể cả tuần không có lịch học
                val availableWeeks = scheduleResponse.data.weeklySchedules
                
                // Tìm tuần hiện tại hoặc tuần đầu tiên nếu không có tuần hiện tại
                val currentWeek = scheduleRepository.getCurrentWeek( semester.semesterCode)
                val selectedWeek = currentWeek ?: availableWeeks.firstOrNull()
                
                _uiState.value = _uiState.value.copy(
                    isLoadingSchedule = false,
                    weeklySchedules = availableWeeks,
                    selectedWeek = selectedWeek,
                    currentWeek = currentWeek, // Track tuần hiện tại
                    currentWeekDisplay = selectedWeek?.let { createWeekDisplay(it) },
                    error = null // Clear error on success
                )

                // Save weekly schedule to database
                scheduleRepository.saveWeeklySchedule(semester.semesterCode)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingSchedule = false,
                    weeklySchedules = emptyList(), // Clear old data on error
                    selectedWeek = null,
                    currentWeek = null,
                    currentWeekDisplay = null,
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
    
    fun getCurrentWeek(): WeeklySchedule? {
        val currentDate = Date()
        return _uiState.value.weeklySchedules
            .filter { it.scheduleItems.isNotEmpty() }
            .find { week ->
                try {
                    val startDate = week.startDate?.let { dateFormat.parse(it) }
                    val endDate = week.endDate?.let { dateFormat.parse(it) }
                    
                    startDate != null && endDate != null &&
                    currentDate.time >= startDate.time && currentDate.time <= endDate.time
                } catch (e: Exception) {
                    false
                }
            }
    }
    
    fun setWeeklySchedules(schedules: List<WeeklySchedule>) {
        _uiState.value = _uiState.value.copy(
            weeklySchedules = schedules, // Hiển thị tất cả tuần, kể cả tuần rỗng
            isLoading = false
        )
        
        // Auto-select current week if available
        getCurrentWeek()?.let { currentWeek ->
            selectWeek(currentWeek)
        } ?: run {
            // If no current week, select first available week
            schedules.firstOrNull()?.let { firstWeek ->
                selectWeek(firstWeek)  
            }
        }
    }
    
    // Hilt automatically manages repository lifecycle
    // No need for onCleared() method
}
