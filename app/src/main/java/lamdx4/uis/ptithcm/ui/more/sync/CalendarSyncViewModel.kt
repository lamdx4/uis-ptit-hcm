package lamdx4.uis.ptithcm.ui.more.sync


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.local.LoginPrefs
import lamdx4.uis.ptithcm.data.model.Semester
import lamdx4.uis.ptithcm.data.repository.CalendarSyncRepository
import lamdx4.uis.ptithcm.data.repository.CalendarSyncRepository.CalendarEventCheckResult
import lamdx4.uis.ptithcm.data.repository.ScheduleRepository
import javax.inject.Inject

@HiltViewModel
class CalendarSyncViewModel @Inject constructor(
    app: Application,
    private val calendarSyncRepository: CalendarSyncRepository,
    private val scheduleRepository: ScheduleRepository,
    private val loginPrefs: LoginPrefs
) : AndroidViewModel(app) {

    companion object {
        private const val TAG = "CalendarSyncViewModel"
    }


    private val _uiState = MutableStateFlow(CalendarSyncUiState())
    val uiState: StateFlow<CalendarSyncUiState> = _uiState.asStateFlow()

    private val _semesters = MutableStateFlow<List<Semester>>(emptyList())
    val semesters: StateFlow<List<Semester>> = _semesters.asStateFlow()

    private val _selectedSemester = MutableStateFlow<Semester?>(null)
    val selectedSemester: StateFlow<Semester?> = _selectedSemester.asStateFlow()

    // State for duplicate event dialog
    private val _duplicateEventsDialogState = MutableStateFlow<CalendarEventCheckResult?>(null)
    val duplicateEventsDialogState: StateFlow<CalendarEventCheckResult?> = _duplicateEventsDialogState.asStateFlow()

    /**
     * Đặt số phút nhắc trước khi sự kiện bắt đầu
     */
    fun setRemindMinutes(minutes: Int) {
        _uiState.value = _uiState.value.copy(remindMinutes = minutes)
    }

    init {
        loadSemesters()
    }

    /**
     * Lấy danh sách học kỳ sử dụng ScheduleRepository (DRY principle)
     */
    fun loadSemesters() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoadingSemesters = true, error = null)

                Log.d(TAG, "Loading semesters using ScheduleRepository...")
                val semesterResponse = scheduleRepository.getSemesters()
                _semesters.value = semesterResponse.data.semesters

                // Chọn học kỳ hiện tại làm mặc định
                val currentSemester = scheduleRepository.getCurrentSemester()
                _selectedSemester.value = currentSemester
                
                _uiState.value = _uiState.value.copy(isLoadingSemesters = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingSemesters = false,
                    error = "Không thể tải danh sách học kỳ: ${e.message}"
                )
            }
        }
    }

    /**
     * Chọn học kỳ để đồng bộ
     */
    fun selectSemester(semester: Semester) {
        _selectedSemester.value = semester
    }

    /**
     * Bắt đầu đồng bộ học kỳ đã chọn
     */
    fun syncSelectedSemester() {
        val semester = _selectedSemester.value ?: run {
            _uiState.value = _uiState.value.copy(error = "Vui lòng chọn học kỳ cần đồng bộ")
            return
        }

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // Step 1: Check for existing calendar
                _uiState.value = _uiState.value.copy(isSyncing = false, syncProgress = "", syncProgressPercent = 0f, error = null)
                val checkResult = calendarSyncRepository.hasEventsInSemesterCalendar(semester.semesterName, semester.semesterCode)
                if (checkResult is CalendarEventCheckResult.HasEvent) {
                    // Show dialog to user, wait for their action
                    _duplicateEventsDialogState.value = checkResult
                    return@launch
                } else {
                    // No calendar, proceed to sync
                    _duplicateEventsDialogState.value = null
                    startSemesterSync(semester)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncProgress = "",
                    syncProgressPercent = 0f,
                    error = "Lỗi không mong muốn: ${e.message}"
                )
            }
        }
    }

    /**
     * Call this when the user chooses to delete existing events and sync
     */
    fun onUserConfirmDeleteAndSync() {
        val semester = _selectedSemester.value ?: return
        _duplicateEventsDialogState.value = null
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            startSemesterSync(semester, deleteOldEvents = true)
        }
    }

    /**
     * Call this when the user chooses to append (not delete) and sync
     */
    fun onUserConfirmAppendAndSync() {
        val semester = _selectedSemester.value ?: return
        _duplicateEventsDialogState.value = null
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            startSemesterSync(semester, deleteOldEvents = false)
        }
    }

    /**
     * Call this if the user cancels the dialog
     */
    fun onUserCancelDuplicateDialog() {
        _duplicateEventsDialogState.value = null
    }

    /**
     * Internal: Actually perform the sync, optionally deleting existing events first
     */
    private suspend fun startSemesterSync(semester: Semester, deleteOldEvents: Boolean = false) {
        try {
            _uiState.value = _uiState.value.copy(
                isSyncing = true,
                syncProgress = "Đang khởi tạo kết nối Google Calendar...",
                error = null,
                lastSyncResult = "",
                syncProgressPercent = 0f
            )

            // Khởi tạo Calendar service
            val isInitialized = calendarSyncRepository.initializeCalendarService()
            if (!isInitialized) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    error = "Không thể kết nối Google Calendar"
                )
                return
            }

            _uiState.value = _uiState.value.copy(
                syncProgress = "Đang tạo/tìm calendar UIS PTIT..."
            )

            // Tìm hoặc tạo calendar

            val calendarId = calendarSyncRepository.getOrCreatePtitCalendar()
            if (calendarId == null) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    error = "Không thể tạo calendar trên Google"
                )
                return
            }

            // Nếu chọn ghi đè thì xoá hết event cũ trước khi sync
            if (deleteOldEvents) {
                _uiState.value = _uiState.value.copy(syncProgress = "Đang xoá toàn bộ sự kiện cũ...")
                calendarSyncRepository.clearAllEventsInCalendar(calendarId)
            }

            _uiState.value = _uiState.value.copy(
                syncProgress = "Đang tải thời khóa biểu học kỳ ${semester.semesterName}..."
            )

            val scheduleResponse = scheduleRepository.getWeeklySchedule(
                semester.semesterCode
            )

            val totalEvents = scheduleResponse.data.weeklySchedules.sumOf { it.scheduleItems.size }
            _uiState.value = _uiState.value.copy(
                syncProgress = "Đang đồng bộ $totalEvents sự kiện...",
                syncProgressPercent = 0f
            )

            // Đồng bộ lên Google Calendar (emit từng event)
            var lastSuccess = 0
            var lastError = 0
            calendarSyncRepository.syncSemesterSchedule(
                semesterCode = semester.semesterCode,
                semesterName = semester.semesterName,
                scheduleResponse = scheduleResponse,
                remindMinutes = _uiState.value.remindMinutes,
                forceDeleteCalendar = deleteOldEvents
            ).collect { progress ->
                lastSuccess = progress.successCount
                lastError = progress.errorCount
                val status = if (progress.isSuccess) "✅" else "❌"
                val eventName = progress.eventTitle ?: ""
                val msg = if (progress.isSuccess) {
                    "$status Đã đồng bộ: $eventName ($lastSuccess/${progress.total})"
                } else {
                    "$status Lỗi: $eventName (${progress.errorMessage ?: ""}) ($lastSuccess thành công, $lastError lỗi)"
                }
                val percent = if (progress.total > 0) (progress.index.coerceAtMost(progress.total).toFloat() / progress.total) else 0f
                _uiState.value = _uiState.value.copy(
                    syncProgress = msg,
                    syncProgressPercent = percent,
                    lastSyncResult = if (lastSuccess + lastError == progress.total) {
                        if (lastError == 0) "✅ Đồng bộ thành công $lastSuccess sự kiện" else "⚠️ $lastSuccess thành công, $lastError lỗi"
                    } else "",
                    isSyncing = lastSuccess + lastError != progress.total,
                    error = if (!progress.isSuccess) progress.errorMessage else null
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isSyncing = false,
                syncProgress = "",
                syncProgressPercent = 0f,
                error = "Lỗi không mong muốn: ${e.message}"
            )
        }
    }

    /**
     * Xóa thông báo lỗi
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Xử lý kết quả xác thực Google thành công - cần accessToken cho Calendar API
     * 
     * LƯU Ý: Hiện tại đang nhận idToken từ Credential Manager API
     * Cần chuyển đổi sang accessToken để gọi được Google Calendar API
     */
    fun onGoogleAuthSuccess(accessToken: String) {
        Log.d(TAG, "Google authentication successful, storing accessToken for Calendar API")
        calendarSyncRepository.setAuthToken(accessToken)
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            hasCalendarPermission = true,
            error = null
        )
        // Semesters đã được load trong init, không cần load lại
    }

    /**
     * Xử lý lỗi xác thực Google
     */
    fun onGoogleAuthError(error: String) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = "Đăng nhập Google thất bại: $error"
        )
    }

    /**
     * Lấy access token từ LoginPrefs
     */
}

data class CalendarSyncUiState(
    val isLoading: Boolean = false, // Semesters load first, no initial loading needed
    val hasCalendarPermission: Boolean = false,
    val isLoadingSemesters: Boolean = false,
    val isSyncing: Boolean = false,
    val syncProgress: String = "",
    val syncProgressPercent: Float = 0f, // 0.0 - 1.0
    val lastSyncResult: String = "",
    val error: String? = null,
    val remindMinutes: Int = 0 // Số phút nhắc trước khi sự kiện bắt đầu
    // Dialog state is now in duplicateEventsDialogState
)
