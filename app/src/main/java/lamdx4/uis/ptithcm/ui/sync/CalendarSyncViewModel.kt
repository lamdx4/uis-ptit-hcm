package lamdx4.uis.ptithcm.ui.sync

import android.app.Application
import android.app.Activity
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.local.LoginPrefs
import lamdx4.uis.ptithcm.data.model.Semester
import lamdx4.uis.ptithcm.data.repository.CalendarSyncRepository
import lamdx4.uis.ptithcm.data.repository.ScheduleRepository
import lamdx4.uis.ptithcm.data.repository.SyncResult
import javax.inject.Inject

@HiltViewModel
class CalendarSyncViewModel @Inject constructor(
    app: Application,
    private val calendarSyncRepository: CalendarSyncRepository,
    private val scheduleRepository: ScheduleRepository
) : AndroidViewModel(app) {

    private val loginPrefs = LoginPrefs(app)

    private val _uiState = MutableStateFlow(CalendarSyncUiState())
    val uiState: StateFlow<CalendarSyncUiState> = _uiState.asStateFlow()

    private val _semesters = MutableStateFlow<List<Semester>>(emptyList())
    val semesters: StateFlow<List<Semester>> = _semesters.asStateFlow()

    private val _selectedSemester = MutableStateFlow<Semester?>(null)
    val selectedSemester: StateFlow<Semester?> = _selectedSemester.asStateFlow()

    init {
        checkCalendarPermission()
    }

    /**
     * Kiểm tra quyền truy cập Google Calendar
     */
    private fun checkCalendarPermission() {
        val hasPermission = calendarSyncRepository.hasCalendarPermission()
        _uiState.value = _uiState.value.copy(
            hasCalendarPermission = hasPermission,
            isLoading = false
        )
    }

    /**
     * Lấy danh sách học kỳ
     */
    fun loadSemesters() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoadingSemesters = true, error = null)
                
                // Lấy access token từ LoginPrefs
                val accessToken = getAccessToken() ?: run {
                    _uiState.value = _uiState.value.copy(
                        isLoadingSemesters = false,
                        error = "Vui lòng đăng nhập lại để lấy thông tin học kỳ"
                    )
                    return@launch
                }

                val semesterResponse = scheduleRepository.getSemesters(accessToken)
                _semesters.value = semesterResponse.data.semesters
                
                // Chọn học kỳ hiện tại làm mặc định
                val currentSemester = scheduleRepository.getCurrentSemester(accessToken)
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

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isSyncing = true,
                    syncProgress = "Đang khởi tạo kết nối Google Calendar...",
                    error = null
                )

                // Khởi tạo Calendar service
                val isInitialized = calendarSyncRepository.initializeCalendarService()
                if (!isInitialized) {
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        error = "Không thể kết nối Google Calendar"
                    )
                    return@launch
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
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    syncProgress = "Đang tải thời khóa biểu học kỳ ${semester.semesterName}..."
                )

                // Lấy thời khóa biểu
                val accessToken = getAccessToken() ?: run {
                    _uiState.value = _uiState.value.copy(
                        isSyncing = false,
                        error = "Phiên đăng nhập hết hạn"
                    )
                    return@launch
                }

                val scheduleResponse = scheduleRepository.getWeeklySchedule(
                    accessToken, 
                    semester.semesterCode
                )

                _uiState.value = _uiState.value.copy(
                    syncProgress = "Đang đồng bộ ${scheduleResponse.data.weeklySchedules.sumOf { it.scheduleItems.size }} sự kiện..."
                )

                // Đồng bộ lên Google Calendar
                val syncResult = calendarSyncRepository.syncSemesterSchedule(
                    semesterCode = semester.semesterCode,
                    semesterName = semester.semesterName,
                    scheduleResponse = scheduleResponse,
                    periodsInfo = scheduleResponse.data.periodsInDay
                )

                // Xử lý kết quả
                when (syncResult) {
                    is SyncResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isSyncing = false,
                            syncProgress = "",
                            lastSyncResult = "✅ Đồng bộ thành công ${syncResult.eventsCreated} sự kiện"
                        )
                    }
                    is SyncResult.PartialSuccess -> {
                        _uiState.value = _uiState.value.copy(
                            isSyncing = false,
                            syncProgress = "",
                            lastSyncResult = "⚠️ Đồng bộ một phần: ${syncResult.eventsCreated} thành công, ${syncResult.errors} lỗi",
                            error = "Một số sự kiện không thể đồng bộ: ${syncResult.errorMessages.take(3).joinToString(", ")}"
                        )
                    }
                    is SyncResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isSyncing = false,
                            syncProgress = "",
                            error = "❌ Đồng bộ thất bại: ${syncResult.message}"
                        )
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSyncing = false,
                    syncProgress = "",
                    error = "Lỗi không mong muốn: ${e.message}"
                )
            }
        }
    }

    /**
     * Xóa thông báo lỗi
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Lấy Google Sign-In Options
     */
    /**
     * Xử lý đăng nhập Google
     */
    fun signInWithGoogle() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val success = calendarSyncRepository.authenticateWithGoogle()
                if (success) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasCalendarPermission = true,
                        error = null
                    )
                    loadSemesters()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Đăng nhập Google thất bại"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Lỗi xác thực: ${e.message}"
                )
            }
        }
    }

    /**
     * Lấy access token từ LoginPrefs
     */
    private suspend fun getAccessToken(): String? {
        return try {
            loginPrefs.accessToken.first()
        } catch (e: Exception) {
            null
        }
    }
}

data class CalendarSyncUiState(
    val isLoading: Boolean = true,
    val hasCalendarPermission: Boolean = false,
    val isLoadingSemesters: Boolean = false,
    val isSyncing: Boolean = false,
    val syncProgress: String = "",
    val lastSyncResult: String = "",
    val error: String? = null
)
