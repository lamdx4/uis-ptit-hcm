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
import lamdx4.uis.ptithcm.data.model.GradeResponse
import lamdx4.uis.ptithcm.data.model.SemesterGrade
import lamdx4.uis.ptithcm.data.model.SubjectGrade
import lamdx4.uis.ptithcm.data.model.GradeStatistics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeRepository @Inject constructor(private val client: HttpClient) {

    // 🎯 Cache data to avoid reloading
    private var cachedGrades: GradeResponse? = null
    private var cachedStatistics: GradeStatistics? = null
    private var lastGradesFetch: Long = 0
    private var lastStatsFetch: Long = 0

    // Cache duration: 10 minutes (grades change less frequently)  
    private val CACHE_DURATION = 10 * 60 * 1000L

    /**
     * Clear all cached data - useful when switching accounts
     */
    fun clearCache() {
        cachedGrades = null
        cachedStatistics = null
        lastGradesFetch = 0L
        lastStatsFetch = 0L
    }

    /**
     * Lấy tất cả điểm của sinh viên qua các học kỳ (với caching)
     */
    suspend fun getAllGrades(accessToken: String): GradeResponse {
        val currentTime = System.currentTimeMillis()

        // 🎯 Check cache first
        cachedGrades?.let { cached ->
            if (currentTime - lastGradesFetch < CACHE_DURATION) {
                return cached
            }
        }

        // 📡 Fetch from API if not cached or expired
        val response = client.post("http://uis.ptithcm.edu.vn/api/srm/w-locdsdiemsinhvien") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            header(HttpHeaders.Accept, "application/json, text/plain, */*")
            header(HttpHeaders.ContentType, ContentType.Text.Plain)
            // Add Cookie if needed based on the curl example
            header(HttpHeaders.Cookie, "ASP.NET_SessionId=hpygoowhw0jposd3gosqw1xn")
            setBody("")
        }.body<GradeResponse>()

        // 💾 Cache the response
        cachedGrades = response
        lastGradesFetch = currentTime

        return response
    }

    /**
     * Lấy điểm theo học kỳ cụ thể
     */
    suspend fun getGradesBySemester(
        accessToken: String,
        semesterCode: String
    ): List<SubjectGrade>? {
        val allGrades = getAllGrades(accessToken)
        return allGrades.data.semesterGrades
            .find { it.semesterCode == semesterCode }
            ?.subjectGrades
    }

    /**
     * Lấy danh sách học kỳ có điểm
     */
    suspend fun getAvailableSemesters(accessToken: String): List<SemesterGrade> {
        val allGrades = getAllGrades(accessToken)
        return allGrades.data.semesterGrades
    }

    /**
     * Lấy thống kê điểm tổng quát (với caching)
     */
    suspend fun getGradeStatistics(accessToken: String): GradeStatistics {
        val currentTime = System.currentTimeMillis()

        // 🎯 Check cache first
        cachedStatistics?.let { cached ->
            if (currentTime - lastStatsFetch < CACHE_DURATION) {
                return cached
            }
        }

        // 📊 Calculate from grades data (uses cached grades if available)
        val allGrades = getAllGrades(accessToken)
        val semesters = allGrades.data.semesterGrades

        // Tính toán thống kê theo loại điểm A, B, C, D, F
        val allSubjects = semesters.flatMap { it.subjectGrades }
        val passedSubjects = allSubjects.filter { it.result == 1 }

        val gradeA = passedSubjects.count { (it.finalGrade4?.toDoubleOrNull() ?: 0.0) >= 3.5 }
        val gradeB = passedSubjects.count {
            val g = it.finalGrade4?.toDoubleOrNull() ?: 0.0; g >= 3.0 && g < 3.5
        }
        val gradeC = passedSubjects.count {
            val g = it.finalGrade4?.toDoubleOrNull() ?: 0.0; g >= 2.0 && g < 3.0
        }
        val gradeD = passedSubjects.count {
            val g = it.finalGrade4?.toDoubleOrNull() ?: 0.0; g >= 1.0 && g < 2.0
        }
        val gradeF = allSubjects.count { it.result == 0 }

        val totalSubjects = allSubjects.size

        val statistics = GradeStatistics(
            so_mon_A = gradeA,
            so_mon_B = gradeB,
            so_mon_C = gradeC,
            so_mon_D = gradeD,
            so_mon_F = gradeF,
            ty_le_A = if (totalSubjects > 0) gradeA * 100.0 / totalSubjects else 0.0,
            ty_le_B = if (totalSubjects > 0) gradeB * 100.0 / totalSubjects else 0.0,
            ty_le_C = if (totalSubjects > 0) gradeC * 100.0 / totalSubjects else 0.0,
            ty_le_D = if (totalSubjects > 0) gradeD * 100.0 / totalSubjects else 0.0,
            ty_le_F = if (totalSubjects > 0) gradeF * 100.0 / totalSubjects else 0.0
        )

        // 💾 Cache the statistics
        cachedStatistics = statistics
        lastStatsFetch = currentTime

        return statistics
    }

    fun close() {
        client.close()
    }
}
