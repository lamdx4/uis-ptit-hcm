package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import lamdx4.uis.ptithcm.data.model.DetailTuitionFeeResponse
import lamdx4.uis.ptithcm.data.model.TotalTuitionFeeResponse
import lamdx4.uis.ptithcm.data.model.TuitionFeeSemestersResponse
import lamdx4.uis.ptithcm.util.CacheEntry
import lamdx4.uis.ptithcm.util.invalidateBearerTokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeeRepository @Inject constructor(
    private val client: HttpClient
) : Cacheable {
    private val cachedTotalTuitionFee =
        mutableMapOf<Pair<Int, Int>, CacheEntry<TotalTuitionFeeResponse>>()
    private val cachedTuitionFeeSemester =
        mutableMapOf<Pair<Int, Int>, CacheEntry<TuitionFeeSemestersResponse>>()
    private val cachedDetailTuitionFee =
        mutableMapOf<Pair<Int, Int>, CacheEntry<DetailTuitionFeeResponse>>()
    private val cacheTimeoutMillis = 5 * 60 * 1000L

    suspend fun getTotalTuitionFee(
        limit: Int = 100,
        page: Int = 1,
        isForceRefresh: Boolean = false
    ): Result<TotalTuitionFeeResponse> {
        val cacheKey = Pair(limit, page)
        val now = System.currentTimeMillis()
        val cached = cachedTotalTuitionFee[cacheKey]

        if (isForceRefresh) {
            cachedTotalTuitionFee.clear()
        } else if (cached != null && now - cached.timestamp < cacheTimeoutMillis) {
            // Cache hit and not expired
            return Result.success(cached.data)
        }

        return try {
            val res =
                this.client.post("https://uis.ptithcm.edu.vn/api/rms/w-locdstonghophocphisv") {
                    contentType(ContentType.Application.Json)
                }.body<TotalTuitionFeeResponse>()
            cachedTotalTuitionFee[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTuitionFeeSemester(
        limit: Int = 100,
        page: Int = 1,
        isForceRefresh: Boolean = false
    ): Result<TuitionFeeSemestersResponse> {
        val cacheKey = Pair(limit, page)
        val now = System.currentTimeMillis()
        val cached = cachedTuitionFeeSemester[cacheKey]

        if (isForceRefresh) {
            cachedTuitionFeeSemester.clear()
        } else if (cached != null && now - cached.timestamp < cacheTimeoutMillis) {
            // Cache hit and not expired
            return Result.success(cached.data)
        }

        return try {
            val res =
                this.client.post("https://uis.ptithcm.edu.vn/api/report/w-locdshockyhocphisinhvien") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        """
                    {
                      "filter": {
                        "is_tieng_anh": null
                      },
                      "additional": {
                        "paging": {
                          "limit": $limit,
                          "page": $page
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
                }.body<TuitionFeeSemestersResponse>()
            cachedTuitionFeeSemester[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDetailTuitionFee(
        limit: Int = 100,
        page: Int = 1,
        semester: Int,
        isForceRefresh: Boolean = false
    ): Result<DetailTuitionFeeResponse> {
        val cacheKey = Pair(limit, page)
        val now = System.currentTimeMillis()
        val cached = cachedDetailTuitionFee[cacheKey]

        if (isForceRefresh) {
            cachedDetailTuitionFee.clear()
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
                        "hoc_ky": $semester
                      },
                      "additional": {
                        "paging": {
                          "limit": $limit,
                          "page": $page
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
                }.body<DetailTuitionFeeResponse>()
            cachedDetailTuitionFee[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearCache() {
        cachedTotalTuitionFee.clear()
        cachedTuitionFeeSemester.clear()
        cachedDetailTuitionFee.clear()
        this.client.invalidateBearerTokens()
    }
}
