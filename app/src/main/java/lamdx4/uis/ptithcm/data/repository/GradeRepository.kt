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

class GradeRepository {
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
     * Lấy tất cả điểm của sinh viên qua các học kỳ
     */
    suspend fun getAllGrades(accessToken: String): GradeResponse {
        return client.post("https://uis.ptithcm.edu.vn/api/srm/w-locdsdiemsinhvien") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            header(HttpHeaders.Accept, "application/json, text/plain, */*")
            header(HttpHeaders.ContentType, ContentType.Text.Plain)
            // Add Cookie if needed based on the curl example
            header(HttpHeaders.Cookie, "ASP.NET_SessionId=hpygoowhw0jposd3gosqw1xn")
            setBody("")
        }.body()
    }

    /**
     * Lấy điểm theo học kỳ cụ thể
     */
    suspend fun getGradesBySemester(accessToken: String, semesterCode: String): List<SubjectGrade>? {
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
     * Lấy thống kê điểm tổng quát
     */
    suspend fun getGradeStatistics(accessToken: String): GradeStatistics {
        val allGrades = getAllGrades(accessToken)
        val semesters = allGrades.data.semesterGrades
        
        val latestSemester = semesters
            .filter { it.cumulativeGpa10 != null }
            .maxByOrNull { it.semesterCode.toIntOrNull() ?: 0 }
        
        val totalCreditsPassedCumulative = latestSemester?.creditsPassedCumulative?.toIntOrNull() ?: 0
        val currentGpa = latestSemester?.cumulativeGpa10?.toDoubleOrNull() ?: 0.0
        val currentGpa4 = latestSemester?.cumulativeGpa4?.toDoubleOrNull() ?: 0.0
        
        // Tính toán các môn theo trạng thái
        val allSubjects = semesters.flatMap { it.subjectGrades }
        val passedSubjects = allSubjects.filter { it.result == 1 }
        val failedSubjects = allSubjects.filter { it.result == 0 }
        val improvementSubjects = allSubjects.filter { it.excludeReason.contains("cải thiện") }
        
        return GradeStatistics(
            currentGpa = currentGpa,
            currentGpa4 = currentGpa4,
            totalCreditsPassedCumulative = totalCreditsPassedCumulative,
            passedSubjectsCount = passedSubjects.size,
            failedSubjectsCount = failedSubjects.size,
            improvementSubjectsCount = improvementSubjects.size,
            latestSemesterRank = latestSemester?.semesterRank ?: ""
        )
    }

    fun close() {
        client.close()
    }
}

/**
 * Data class for grade statistics
 */
data class GradeStatistics(
    val currentGpa: Double,
    val currentGpa4: Double,
    val totalCreditsPassedCumulative: Int,
    val passedSubjectsCount: Int,
    val failedSubjectsCount: Int,
    val improvementSubjectsCount: Int,
    val latestSemesterRank: String
)
