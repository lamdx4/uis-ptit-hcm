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

class ScheduleRepository {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
        }
    }

    /**
     * Lấy danh sách học kỳ có thời khóa biểu
     * API: w-locdshockytkbuser
     */
    suspend fun getSemesters(accessToken: String): SemesterResponse {
        return client.post("http://uis.ptithcm.edu.vn/api/sch/w-locdshockytkbuser") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            header(HttpHeaders.Accept, "application/json, text/plain, */*")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Cookie, "ASP.NET_SessionId=hpygoowhw0jposd3gosqw1xn")
            
            setBody("""
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
            """.trimIndent())
        }.body()
    }

    /**
     * Lấy thời khóa biểu tuần theo học kỳ
     * API: w-locdstkbtuanusertheohocky
     */
    suspend fun getWeeklySchedule(accessToken: String, semesterCode: Int): ScheduleResponse {
        return client.post("http://uis.ptithcm.edu.vn/api/sch/w-locdstkbtuanusertheohocky") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            header(HttpHeaders.Accept, "application/json, text/plain, */*")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            header(HttpHeaders.Cookie, "ASP.NET_SessionId=hpygoowhw0jposd3gosqw1xn")
            
            setBody("""
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
            """.trimIndent())
        }.body()
    }

    /**
     * Lấy danh sách các tuần có lịch học (bao gồm cả tuần rỗng)
     */
    suspend fun getAvailableWeeks(accessToken: String, semesterCode: Int): List<WeeklySchedule> {
        val scheduleResponse = getWeeklySchedule(accessToken, semesterCode)
        // Hiển thị tất cả tuần, kể cả tuần không có môn học
        return scheduleResponse.data.weeklySchedules
    }

    /**
     * Lấy học kỳ hiện tại dựa trên ngày
     * Sử dụng hoc_ky_theo_ngay_hien_tai từ API
     */
    suspend fun getCurrentSemester(accessToken: String): Semester? {
        val semesterResponse = getSemesters(accessToken)
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
    suspend fun getCurrentWeek(accessToken: String, semesterCode: Int): WeeklySchedule? {
        val scheduleResponse = getWeeklySchedule(accessToken, semesterCode)
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

    fun close() {
        client.close()
    }
}
