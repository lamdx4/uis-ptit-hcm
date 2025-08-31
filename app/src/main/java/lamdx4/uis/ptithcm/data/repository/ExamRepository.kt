package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import lamdx4.uis.ptithcm.data.database.AlarmDao
import lamdx4.uis.ptithcm.data.model.AlarmEntity
import lamdx4.uis.ptithcm.data.model.ExamResponse
import lamdx4.uis.ptithcm.data.model.ExamSemesterResponse
import lamdx4.uis.ptithcm.data.model.ExamSubTypeResponse
import lamdx4.uis.ptithcm.data.model.ExamTypeResponse
import lamdx4.uis.ptithcm.data.model.SubTypeExamResponse
import lamdx4.uis.ptithcm.util.CacheEntry
import lamdx4.uis.ptithcm.util.invalidateBearerTokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
    private val client: HttpClient,
    private val alarmDao: AlarmDao
) : Cacheable {

    suspend fun insertAlarm(alarm: AlarmEntity) = alarmDao.insertAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmEntity) = alarmDao.updateAlarm(alarm)
    suspend fun deleteAlarm(alarm: AlarmEntity) = alarmDao.deleteAlarm(alarm)
    suspend fun getAllAlarms() = alarmDao.getAllAlarms()
    suspend fun getAlarmById(id: Int) = alarmDao.getAlarmById(id)

    private val cachedPersonalExams = mutableMapOf<Pair<Int, Int>, CacheEntry<ExamResponse>>()
    private val cachedExamSemester =
        mutableMapOf<Pair<Int, Int>, CacheEntry<ExamSemesterResponse>>()
    private val cachedExamTypes = mutableMapOf<Pair<Int, Int>, CacheEntry<ExamTypeResponse>>()
    private val cachedExamSubTypes = mutableMapOf<Pair<Int, Int>, CacheEntry<ExamSubTypeResponse>>()
    private val cachedSubTypeExams = mutableMapOf<Pair<Int, Int>, CacheEntry<SubTypeExamResponse>>()
    private val cacheTimeoutMillis = 5 * 60 * 1000L

    suspend fun getPersonalExams(
        semester: Int,
        isForceRefresh: Boolean = false
    ): Result<ExamResponse> {
        val cacheKey = Pair(100, 1)
        val now = System.currentTimeMillis()
        val cached = cachedPersonalExams[cacheKey]

        if (isForceRefresh) {
            cachedPersonalExams.clear()
        } else if (cached != null && now - cached.timestamp < cacheTimeoutMillis) {
            // Cache hit and not expired
            return Result.success(cached.data)
        }

        return try {
            val res =
                this.client.post("https://uis.ptithcm.edu.vn/api/epm/w-locdslichthisvtheohocky") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """
                         {
                           "filter": {
                             "hoc_ky": $semester,
                             "is_giua_ky": false
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
                }.body<ExamResponse>()
            cachedPersonalExams[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExamSemesters(
        isForceRefresh: Boolean = false
    ): Result<ExamSemesterResponse> {
        val cacheKey = Pair(100, 1)
        val now = System.currentTimeMillis()
        val cached = cachedExamSemester[cacheKey]

        if (isForceRefresh) {
            cachedExamSemester.clear()
        } else if (cached != null && now - cached.timestamp < cacheTimeoutMillis) {
            // Cache hit and not expired
            return Result.success(cached.data)
        }

        return try {
            val res =
                this.client.post("https://uis.ptithcm.edu.vn/api/report/w-locdshockylichthisinhvien") {
                    contentType(ContentType.Application.Json)
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
                            "name": null,
                            "order_type": 1
                          }
                        ]
                      }
                    }
                """.trimIndent()
                    )
                }.body<ExamSemesterResponse>()
            cachedExamSemester[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExamTypes(
        isForceRefresh: Boolean = false
    ): Result<ExamTypeResponse> {
        val cacheKey = Pair(100, 1)
        val now = System.currentTimeMillis()
        val cached = cachedExamTypes[cacheKey]

        if (isForceRefresh) {
            cachedExamTypes.clear()
        } else if (cached != null && now - cached.timestamp < cacheTimeoutMillis) {
            // Cache hit and not expired
            return Result.success(cached.data)
        }

        return try {
            val res =
                this.client.post("https://uis.ptithcm.edu.vn/api/epm/w-locdsdoituongxemlichthi?is_dk_coi_thi=false") {
                    contentType(ContentType.Application.Json)
                }.body<ExamTypeResponse>()
            cachedExamTypes[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getExamSubTypes(
        semester: Int,
        examType: Int,
        isForceRefresh: Boolean = false
    ): Result<ExamSubTypeResponse> {
        val cacheKey = Pair(100, 1)
        val now = System.currentTimeMillis()
        val cached = cachedExamSubTypes[cacheKey]

        if (isForceRefresh) {
            cachedExamSubTypes.clear()
        } else if (cached != null && now - cached.timestamp < cacheTimeoutMillis) {
            // Cache hit and not expired
            return Result.success(cached.data)
        }

        return try {
            val res =
                this.client.post("https://uis.ptithcm.edu.vn/api/epm/w-locdsdulieutheodoituonglichthi") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """
                    {
                      "hoc_ky": $semester,
                      "loai_doi_tuong": $examType,
                      "is_dk_coi_thi": false
                    }
                """.trimIndent()
                    )
                }.body<ExamSubTypeResponse>()
            cachedExamSubTypes[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSubTypeExams(
        semester: Int,
        examType: Int,
        subType: String = "",
        examDate: String = "",
        isForceRefresh: Boolean = false
    ): Result<SubTypeExamResponse> {
        val cacheKey = Pair(100, 1)
        val now = System.currentTimeMillis()
        val cached = cachedSubTypeExams[cacheKey]

        if (isForceRefresh) {
            cachedSubTypeExams.clear()
        } else if (cached != null && now - cached.timestamp < cacheTimeoutMillis) {
            // Cache hit and not expired
            return Result.success(cached.data)
        }

        return try {
            val res =
                this.client.post("https://uis.ptithcm.edu.vn/api/epm/w-loclichthitonghoptheodoituong") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """
                    {
                      "hoc_ky": $semester,
                      "loai_doi_tuong": $examType,,
                      "id_du_lieu": $subType,
                      "ngay_thi": $examDate,
                      "is_dk_coi_thi": false,
                      "is_giua_ky": false,
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
                }.body<SubTypeExamResponse>()
            cachedSubTypeExams[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearCache() {
        cachedPersonalExams.clear()
        cachedExamSemester.clear()
        cachedExamTypes.clear()
        cachedExamSubTypes.clear()
        cachedSubTypeExams.clear()
        this.client.invalidateBearerTokens()
    }
}