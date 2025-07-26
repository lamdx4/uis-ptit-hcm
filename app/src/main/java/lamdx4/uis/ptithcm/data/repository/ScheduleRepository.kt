package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.call.body
import kotlinx.serialization.json.Json
import lamdx4.uis.ptithcm.data.model.SemesterResponse
import lamdx4.uis.ptithcm.data.model.ScheduleResponse
import lamdx4.uis.ptithcm.data.model.Semester
import lamdx4.uis.ptithcm.data.model.WeeklySchedule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor(private val client: HttpClient) {

    // 🎯 Cache data to avoid reloading
    private var cachedSemesters: SemesterResponse? = null
    private val cachedSchedules = mutableMapOf<String, ScheduleResponse>()
    private var lastSemestersFetch: Long = 0
    private val scheduleLastFetch = mutableMapOf<String, Long>()

    // Cache duration: 5 minutes
    private val CACHE_DURATION = 5 * 60 * 1000L

    /**
     * Clear all cached data - useful when switching accounts
     */
    fun clearCache() {
        cachedSemesters = null
        cachedSchedules.clear()
        lastSemestersFetch = 0L
        scheduleLastFetch.clear()
    }


    /**
     * Lấy danh sách học kỳ có thời khóa biểu (với caching)
     * API: w-locdshockytkbuser
     */
    suspend fun getSemesters(): SemesterResponse {
        val currentTime = System.currentTimeMillis()

        // 🎯 Check cache first
        cachedSemesters?.let { cached ->
            if (currentTime - lastSemestersFetch < CACHE_DURATION) {
                return cached
            }
        }

        // 📡 Fetch from API if not cached or expired
        val response = client.post("http://uis.ptithcm.edu.vn/api/sch/w-locdshockytkbuser") {
            header(HttpHeaders.Accept, "application/json, text/plain, */*")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Cookie, "ASP.NET_SessionId=hpygoowhw0jposd3gosqw1xn")

            setBody(
                """
                {
                    "filter": {
                        "is_tieng_anh": null
                    },
                    "additional": {
                        "paging": {
                            "limit": 100,
                            "page": 1
                        },
                        "ordering": [
                            {
                                "name": "hoc_ky",
                                "order_type": 1
                            }
                        ]
                    }
                }
            """.trimIndent()
            )
        }.body<SemesterResponse>()

        // 💾 Cache the response
        cachedSemesters = response
        lastSemestersFetch = currentTime

        return response
    }

    /**
     * Lấy thời khóa biểu tuần theo học kỳ (với caching)
     * API: w-locdstkbtuanusertheohocky
     */
    suspend fun getWeeklySchedule(semesterCode: Int): ScheduleResponse {
        val cacheKey = semesterCode.toString()
        val currentTime = System.currentTimeMillis()

        // 🎯 Check cache first
        cachedSchedules[cacheKey]?.let { cached ->
            val lastFetch = scheduleLastFetch[cacheKey] ?: 0
            if (currentTime - lastFetch < CACHE_DURATION) {
                return cached
            }
        }

        // 📡 Fetch from API if not cached or expired
        val response =
            client.post("http://uis.ptithcm.edu.vn/api/sch/w-locdstkbtuanusertheohocky") {
                header(HttpHeaders.Accept, "application/json, text/plain, */*")
                header(HttpHeaders.ContentType, ContentType.Application.Json)

                setBody(
                    """
                {
                    "filter": {
                        "hoc_ky": $semesterCode,
                        "ten_hoc_ky": ""
                    },
                    "additional": {
                        "paging": {
                            "limit": 100,
                            "page": 1
                        },
                        "ordering": [
                            {
                                "name": null,
                                "order_type": null
                            }
                        ]
                    }
                }
            """.trimIndent()
                )
            }.body<ScheduleResponse>()

        // 💾 Cache the response
        cachedSchedules[cacheKey] = response
        scheduleLastFetch[cacheKey] = currentTime

        return response
    }

    /**
     * Lấy danh sách các tuần có lịch học (bao gồm cả tuần rỗng)
     */
    suspend fun getAvailableWeeks(semesterCode: Int): List<WeeklySchedule> {
        val scheduleResponse = getWeeklySchedule(semesterCode)
        // Hiển thị tất cả tuần, kể cả tuần không có môn học
        return scheduleResponse.data.weeklySchedules
    }

    /**
     * Lấy học kỳ hiện tại dựa trên ngày
     * Sử dụng hoc_ky_theo_ngay_hien_tai từ API
     */
    suspend fun getCurrentSemester(): Semester? {
        val semesterResponse = getSemesters()
        val currentSemesterCode = semesterResponse.data.currentSemesterByDate

        return if (currentSemesterCode > 0) {
            semesterResponse.data.semesters.find { it.semesterCode == currentSemesterCode }
        } else {
            // Nếu không có semester hiện tại theo ngày, lấy semester mới nhất
            semesterResponse.data.semesters.firstOrNull()
        }
    }

    /**
     * Lấy tuần hiện tại dựa trên ngày hiện tại và ngày bắt đầu học kỳ
     */
    suspend fun getCurrentWeek(semesterCode: Int): WeeklySchedule? {
        val scheduleResponse = getWeeklySchedule(semesterCode)
        val currentDate = System.currentTimeMillis()
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())

        // Tìm tuần hiện tại dựa trên ngày (bao gồm cả tuần không có lịch học)
        return scheduleResponse.data.weeklySchedules
            .find { week ->
                try {
                    val startDate = week.startDate?.let { dateFormat.parse(it) }
                    val endDate = week.endDate?.let { dateFormat.parse(it) }

                    if (startDate != null && endDate != null) {
                        currentDate >= startDate.time && currentDate <= endDate.time
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    false
                }
            }
    }

    fun clearScheduleCache(semesterCode: Int? = null) {
        if (semesterCode != null) {
            val key = semesterCode.toString()
            cachedSchedules.remove(key)
            scheduleLastFetch.remove(key)
        } else {
            cachedSchedules.clear()
            scheduleLastFetch.clear()
        }
    }

    fun close() {
        client.close()
    }
}
