package lamdx4.uis.ptithcm.ui.sync

/*
 * KIẾN TRÚC ĐỒNG BỘ GOOGLE CALENDAR - HƯỚNG DẪN TRIỂN KHAI
 * 
 * NGUYÊN TẮC DRY (Don't Repeat Yourself):
 * - ✅ Đã sử dụng ScheduleRepository.getSemesters() thay vì duplicate logic
 * - ✅ Tái sử dụng getCurrentSemester() từ ScheduleRepository
 * - ✅ Loại bỏ code trùng lặp trong việc load semesters
 * 
 * LUỒNG CHÍNH XÁC:
 * 1. Load semesters từ ScheduleRepository (sử dụng UIS PTIT access token)
 * 2. User chọn semester cần đồng bộ  
 * 3. Xác thực Google để lấy accessToken (KHÔNG phải idToken)
 * 4. Sử dụng accessToken để gọi Google Calendar API
 * 
 * VẤN ĐỀ HIỆN TẠI:
 * - Credential Manager API chỉ cung cấp idToken, không phải accessToken
 * - idToken chỉ dùng để xác thực danh tính, KHÔNG thể gọi Calendar API
 * - Cần accessToken với scope 'https://www.googleapis.com/auth/calendar'
 * 
 * GIẢI PHÁP:
 * - Sử dụng GoogleSignInClient hoặc OAuth 2.0 flow để lấy accessToken
 * - Hoặc trao đổi idToken lấy accessToken qua Google OAuth endpoint
 * - Cấu hình scope Calendar trong OAuth consent screen (đã làm)
 */

import android.app.Application
import android.util.Log
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
                
                // Lấy access token từ LoginPrefs
                val accessToken = getAccessToken() ?: run {
                    _uiState.value = _uiState.value.copy(
                        isLoadingSemesters = false,
                        error = "Vui lòng đăng nhập lại để lấy thông tin học kỳ"
                    )
                    return@launch
                }

                Log.d(TAG, "Loading semesters using ScheduleRepository...")
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
    private suspend fun getAccessToken(): String? {
        return try {
            loginPrefs.accessToken.first()
        } catch (e: Exception) {
            null
        }
    }
}

data class CalendarSyncUiState(
    val isLoading: Boolean = false, // Semesters load first, no initial loading needed
    val hasCalendarPermission: Boolean = false,
    val isLoadingSemesters: Boolean = false,
    val isSyncing: Boolean = false,
    val syncProgress: String = "",
    val lastSyncResult: String = "",
    val error: String? = null
)
