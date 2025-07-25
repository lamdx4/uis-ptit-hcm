package lamdx4.uis.ptithcm.data.repository

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import lamdx4.uis.ptithcm.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarSyncRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "CalendarSyncRepository"
        private const val CALENDAR_NAME = "UIS PTIT HCM - Thời khóa biểu"
    }

    private var currentIdToken: String? = null

    /**
     * Lưu ID token sau khi xác thực thành công
     */
    fun setAuthToken(idToken: String) {
        currentIdToken = idToken
        Log.i(TAG, "Google ID token saved successfully")
    }

    /**
     * Khởi tạo Google Calendar service với tài khoản đã đăng nhập
     */
    suspend fun initializeCalendarService(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (currentIdToken == null) {
                Log.e(TAG, "No valid ID token available")
                return@withContext false
            }

            // TODO: Khởi tạo Google Calendar API service với access token
            Log.i(TAG, "Calendar service initialized successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize calendar service", e)
            false
        }
    }

    /**
     * Tìm hoặc tạo calendar riêng cho UIS PTIT
     */
    suspend fun getOrCreatePtitCalendar(): String? = withContext(Dispatchers.IO) {
        try {
            // Simulate finding/creating calendar
            delay(1000)
            Log.i(TAG, "Found/Created PTIT calendar")
            "ptit-calendar-id"
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get or create PTIT calendar", e)
            null
        }
    }

    /**
     * Xóa tất cả events trong calendar của một học kỳ cụ thể
     */
    private suspend fun clearSemesterEvents(semesterCode: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            // Simulate clearing events
            delay(500)
            Log.i(TAG, "Cleared events for semester $semesterCode")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear semester events", e)
            false
        }
    }

    /**
     * Đồng bộ thời khóa biểu của một học kỳ lên Google Calendar
     */
    suspend fun syncSemesterSchedule(
        semesterCode: Int,
        semesterName: String,
        scheduleResponse: ScheduleResponse,
        periodsInfo: List<PeriodInfo>
    ): SyncResult = withContext(Dispatchers.IO) {
        try {
            var successCount = 0
            var errorCount = 0
            val errors = mutableListOf<String>()

            // Xóa events cũ của học kỳ này trước
            clearSemesterEvents(semesterCode)

            // Simulate creating events
            scheduleResponse.data.weeklySchedules.forEach { week ->
                week.scheduleItems.forEach { scheduleItem ->
                    try {
                        // Simulate event creation
                        delay(100)
                        successCount++
                        Log.d(TAG, "Created event: ${scheduleItem.subjectName} - Week ${week.semesterWeek}")
                    } catch (e: Exception) {
                        errorCount++
                        val errorMsg = "Failed to create event for ${scheduleItem.subjectName}: ${e.message}"
                        errors.add(errorMsg)
                        Log.e(TAG, errorMsg, e)
                    }
                }
            }

            Log.i(TAG, "Sync completed: $successCount success, $errorCount errors")
            
            if (errorCount == 0) {
                SyncResult.Success(successCount)
            } else {
                SyncResult.PartialSuccess(successCount, errorCount, errors)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync semester schedule", e)
            SyncResult.Error("Sync failed: ${e.message}")
        }
    }

    /**
     * Kiểm tra quyền truy cập Google Calendar
     */
    fun hasCalendarPermission(): Boolean {
        return currentIdToken != null
    }

    /**
     * Đăng xuất và xóa thông tin xác thực
     */
    fun signOut() {
        currentIdToken = null
        Log.i(TAG, "User signed out successfully")
    }

    /**
     * Lấy thông tin người dùng hiện tại
     */
    fun getCurrentUserInfo(): String? {
        return if (currentIdToken != null) {
            "Google User" // TODO: Parse actual user info from ID token
        } else {
            null
        }
    }
}

/**
 * Kết quả đồng bộ
 */
sealed class SyncResult {
    data class Success(val eventsCreated: Int) : SyncResult()
    data class PartialSuccess(val eventsCreated: Int, val errors: Int, val errorMessages: List<String>) : SyncResult()
    data class Error(val message: String) : SyncResult()
}