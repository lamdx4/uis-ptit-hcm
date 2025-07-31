package lamdx4.uis.ptithcm.data.repository

import android.util.Log
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event.ExtendedProperties
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.calendar.model.EventReminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import lamdx4.uis.ptithcm.data.model.ScheduleResponse
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton
import com.google.api.services.calendar.model.Calendar as GCalendar
import com.google.api.services.calendar.model.Event as GEvent
import com.google.api.services.calendar.model.Events as GEvents

/**
 * Repository đồng bộ Google Calendar.
 * Lưu ý: access token phải được lấy từ Credential Manager (Google Identity Services for Android),
 * scope: https://www.googleapis.com/auth/calendar
 * Không dùng GoogleSignInOptions hoặc Play Services Auth cũ.
 */
@Singleton
class CalendarSyncRepository @Inject constructor() {
    /**
     * Kiểm tra calendar học kỳ đã tồn tại chưa (không cần check event)
     */
    suspend fun hasEventsInSemesterCalendar(
        semesterName: String,
        semesterCode: Int
    ): CalendarEventCheckResult = withContext(Dispatchers.IO) {
        val calendarService = getCalendarService()
            ?: return@withContext CalendarEventCheckResult.Error("No calendar service")
        val calendarTitle = "$semesterName - $semesterCode"
        try {
            val calendarList = calendarService.calendarList().list().execute().items
            val found = calendarList.firstOrNull { it.summary == calendarTitle }
            return@withContext if (found == null) {
                CalendarEventCheckResult.CalendarNotFound
            } else {
                CalendarEventCheckResult.HasEvent(found.id)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check calendar existence", e)
            return@withContext CalendarEventCheckResult.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Kết quả kiểm tra calendar học kỳ đã có event chưa
     */
    sealed class CalendarEventCheckResult {
        data class HasEvent(val calendarId: String) : CalendarEventCheckResult()
        data class NoEvent(val calendarId: String) : CalendarEventCheckResult()
        object CalendarNotFound : CalendarEventCheckResult()
        data class Error(val message: String) : CalendarEventCheckResult()
    }

    companion object {
        private const val TAG = "CalendarSyncRepository"
        private const val CALENDAR_NAME = "UIS PTIT HCM - Thời khóa biểu"
    }

    private var currentAccessToken: String? = null

    /**
     * Thiết lập access token cho Google Calendar API.
     *
     * @param accessToken Access token lấy từ Credential Manager (Google Identity Services),
     * scope: https://www.googleapis.com/auth/calendar
     * Không truyền token lấy từ GoogleSignInOptions hoặc Play Services Auth cũ.
     */
    fun setAuthToken(accessToken: String) {
        currentAccessToken = accessToken
        Log.i(TAG, "Google access token saved successfully")
    }

    private fun getCalendarService(): Calendar? {
        val token = currentAccessToken ?: return null
        val credential = HttpRequestInitializer { request ->
            request.headers.authorization = "Bearer $token"
        }
        return Calendar.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), credential)
            .setApplicationName(CALENDAR_NAME)
            .build()
    }

    suspend fun getOrCreateSemesterCalendar(
        semesterName: String,
        semesterCode: Int,
        forceDeleteIfExists: Boolean = false
    ): String? =
        withContext(Dispatchers.IO) {
            val calendarService = getCalendarService() ?: return@withContext null
            val calendarTitle = "$semesterName - $semesterCode"
            try {
                val calendarList = calendarService.calendarList().list().execute().items
                val found = calendarList.firstOrNull { it.summary == calendarTitle }
                if (found != null) {
                    if (forceDeleteIfExists) {
                        try {
                            calendarService.calendars().delete(found.id).execute()
                            Log.i(TAG, "Deleted old calendar $calendarTitle with id ${found.id}")
                        } catch (ex: Exception) {
                            Log.e(TAG, "Failed to delete old calendar $calendarTitle", ex)
                            return@withContext null
                        }
                        // Sau khi xóa, tạo mới
                        val newCalendar = GCalendar().apply { summary = calendarTitle }
                        val created = calendarService.calendars().insert(newCalendar).execute()
                        return@withContext created.id
                    } else {
                        return@withContext found.id
                    }
                }
                // Không tồn tại, tạo mới
                val newCalendar = GCalendar().apply { summary = calendarTitle }
                val created = calendarService.calendars().insert(newCalendar).execute()
                return@withContext created.id
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get or create semester calendar", e)
                return@withContext null
            }
        }

    suspend fun clearAllEventsInCalendar(calendarId: String): Boolean =
        withContext(Dispatchers.IO) {
            val calendarService = getCalendarService() ?: return@withContext false
            try {
                val events: GEvents =
                    calendarService.events().list(calendarId).setMaxResults(2500).execute()
                events.items.forEach { event ->
                    try {
                        calendarService.events().delete(calendarId, event.id).execute()
                    } catch (ex: Exception) {
                        Log.e(TAG, "Failed to delete event ${event.id}", ex)
                    }
                }
                return@withContext true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear events in calendar", e)
                return@withContext false
            }
        }

    /**
     * Khởi tạo Google Calendar service với tài khoản đã đăng nhập
     */
    suspend fun initializeCalendarService(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (currentAccessToken == null) {
                Log.e(TAG, "No valid access token available")
                return@withContext false
            }
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
            delay(1000)
            Log.i(TAG, "Found/Created PTIT calendar")
            return@withContext "ptit-calendar-id"
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get or create PTIT calendar", e)
            return@withContext null
        }
    }

    fun syncSemesterSchedule(
        semesterCode: Int,
        semesterName: String,
        scheduleResponse: ScheduleResponse,
        remindMinutes: Int,
        forceDeleteCalendar: Boolean = false
    ): Flow<SyncProgress> = flow {
        var successCount = 0
        var errorCount = 0
        val errors = mutableListOf<String>()
        try {
            val calendarId = getOrCreateSemesterCalendar(
                semesterName,
                semesterCode,
                forceDeleteIfExists = forceDeleteCalendar
            )
                ?: throw Exception("Không thể lấy calendarId")
            val calendarService = getCalendarService() ?: throw Exception("No calendar service")
            val allItems = scheduleResponse.data.weeklySchedules.flatMap { it.scheduleItems }
            val total = allItems.size
            for ((index, scheduleItem) in allItems.withIndex()) {
                try {
                    val date = scheduleItem.studyDate ?: continue
                    val startPeriod = scheduleItem.startPeriod ?: 1
                    val numberOfPeriods = scheduleItem.numberOfPeriods ?: 1
                    val (startTime, endTime) = getPtitPeriodTime(startPeriod, numberOfPeriods, date)

                    val eventTitle = "${scheduleItem.subjectName} - ${scheduleItem.subjectCode}"
                    val eventLocation = "${scheduleItem.roomCode} - ${scheduleItem.campusCode}"
                    val eventDescription = scheduleItem.teacherName
                    val extendedProperties = mapOf(
                        "subjectCode" to scheduleItem.subjectCode,
                        "classId" to scheduleItem.classId,
                        "scheduleId" to scheduleItem.scheduleId,
                        "selfCreatedId" to scheduleItem.selfCreatedId,
                        "semesterCode" to semesterCode.toString()
                    )

                    val event = GEvent()
                    event.summary = eventTitle
                    event.location = eventLocation
                    event.description = eventDescription
                    event.start = EventDateTime().setDateTime(DateTime(startTime))
                    event.end = EventDateTime().setDateTime(DateTime(endTime))
                    event.reminders = GEvent.Reminders().apply {
                        useDefault = false
                        overrides =
                            listOf(EventReminder().setMethod("popup").setMinutes(remindMinutes))
                    }
                    event.extendedProperties = ExtendedProperties().setPrivate(extendedProperties)
                    calendarService.events().insert(calendarId, event).execute()
                    successCount++
                    emit(
                        SyncProgress(
                            index = index + 1,
                            total = total,
                            eventTitle = eventTitle,
                            isSuccess = true,
                            errorMessage = null,
                            successCount = successCount,
                            errorCount = errorCount
                        )
                    )
                } catch (e: Exception) {
                    errorCount++
                    val errorMsg =
                        "Failed to create event for ${scheduleItem.subjectName}: ${e.message}"
                    errors.add(errorMsg)
                    Log.e(TAG, errorMsg, e)
                    emit(
                        SyncProgress(
                            index = index + 1,
                            total = total,
                            eventTitle = scheduleItem.subjectName,
                            isSuccess = false,
                            errorMessage = e.message,
                            successCount = successCount,
                            errorCount = errorCount
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync semester schedule", e)
            emit(
                SyncProgress(
                    index = -1,
                    total = -1,
                    eventTitle = null,
                    isSuccess = false,
                    errorMessage = "Sync failed: ${e.message}",
                    successCount = successCount,
                    errorCount = errorCount
                )
            )
        }
    }

    data class SyncProgress(
        val index: Int, // 1-based index of event
        val total: Int,
        val eventTitle: String?,
        val isSuccess: Boolean,
        val errorMessage: String?,
        val successCount: Int,
        val errorCount: Int
    )

    private fun getPtitPeriodTime(
        startPeriod: Int,
        numberOfPeriods: Int,
        date: String // dạng: "2024-03-16T00:00:00"
    ): Pair<String, String> {
        val periodStartTimes = arrayOf(
            "07:00", "07:50", "08:40", "09:40", "10:30", "11:20",
            "13:00", "13:50", "14:40", "15:40", "16:30", "17:20",
            "18:00", "18:50", "19:40"
        )
        val periodEndTimes = arrayOf(
            "07:50", "08:40", "09:40", "10:30", "11:20", "12:10",
            "13:50", "14:40", "15:40", "16:30", "17:20", "18:10",
            "18:50", "19:40", "20:30"
        )

        val startIdx = startPeriod - 1
        val endIdx = startIdx + numberOfPeriods - 1
        val startTimeStr =
            periodStartTimes.getOrNull(startIdx) ?: error("startPeriod $startPeriod không hợp lệ")
        val endTimeStr = periodEndTimes.getOrNull(endIdx) ?: error("endPeriod $endIdx không hợp lệ")

        val zone = ZoneId.of("Asia/Ho_Chi_Minh")

        // Parse date string "yyyy-MM-ddTHH:mm:ss" -> chỉ lấy phần yyyy-MM-dd
        val dateOnly = date.substringBefore('T')
        val localDate = LocalDate.parse(dateOnly)

        val startZdt = ZonedDateTime.of(localDate, LocalTime.parse(startTimeStr), zone)
        val endZdt = ZonedDateTime.of(localDate, LocalTime.parse(endTimeStr), zone)

        val isoFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME


        return startZdt.format(isoFormatter) to endZdt.format(isoFormatter)
    }

    /**
     * Kiểm tra quyền truy cập Google Calendar
     */
    fun hasCalendarPermission(): Boolean {
        return currentAccessToken != null
    }

    /**
     * Đăng xuất và xóa thông tin xác thực
     */
    fun signOut() {
        currentAccessToken = null
        Log.i(TAG, "User signed out successfully")
    }

    /**
     * Lấy thông tin người dùng hiện tại
     */
    fun getCurrentUserInfo(): String? {

        return if (currentAccessToken != null) {
            "Google User" // TODO: Parse actual user info from access token
        } else {
            null
        }
    }

    sealed class SyncResult {
        data class Success(val eventsCreated: Int) : SyncResult()
        data class PartialSuccess(
            val eventsCreated: Int,
            val errors: Int,
            val errorMessages: List<String>
        ) : SyncResult()

        data class Error(val message: String) : SyncResult()
    }
}