package lamdx4.uis.ptithcm.ui.schedule

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import lamdx4.uis.ptithcm.data.model.WeeklySchedule
import lamdx4.uis.ptithcm.data.model.WeekScheduleDisplay
import lamdx4.uis.ptithcm.data.model.DaySchedule
import lamdx4.uis.ptithcm.data.model.ScheduleItem
import java.text.SimpleDateFormat
import java.util.*

data class WeeklyScheduleUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val weeklySchedules: List<WeeklySchedule> = emptyList(),
    val selectedWeek: WeeklySchedule? = null,
    val currentWeekDisplay: WeekScheduleDisplay? = null
)

class WeeklyScheduleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WeeklyScheduleUiState())
    val uiState: StateFlow<WeeklyScheduleUiState> = _uiState.asStateFlow()

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun loadSchedule(accessToken: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        // TODO: Implement API call to fetch schedule
        // For now, simulate loading
        // apiService.getWeeklySchedule(accessToken)
        
        // Simulate success after API implementation
        _uiState.value = _uiState.value.copy(isLoading = false)
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
