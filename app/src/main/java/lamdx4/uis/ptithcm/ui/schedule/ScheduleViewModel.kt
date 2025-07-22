package lamdx4.uis.ptithcm.ui.schedule

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import lamdx4.uis.ptithcm.data.model.ScheduleEvent

data class ScheduleState(
    val events: List<ScheduleEvent> = emptyList(),
    val isSyncing: Boolean = false
)

class ScheduleViewModel: ViewModel() {
    private val _state = MutableStateFlow(ScheduleState())
    val state: StateFlow<ScheduleState> = _state

    // TODO: Kết nối repository để lấy dữ liệu thực tế
    init {
        // Dữ liệu mẫu:
        _state.value = ScheduleState(
            events = listOf(
                ScheduleEvent(
                    title = "Lập trình Web",
                    room = "2E27-2E27",
                    startTime = "2025-01-06T07:00",
                    endTime = "2025-01-06T11:00"
                )
            )
        )
    }

    fun syncWithGoogleCalendar() {
        // TODO: Gọi logic đồng bộ Google Calendar
    }
}