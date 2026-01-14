package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import lamdx4.uis.ptithcm.data.model.PrerequisiteResponse
import lamdx4.uis.ptithcm.data.model.PrerequisitesTypeResponse
import lamdx4.uis.ptithcm.util.CacheEntry
import lamdx4.uis.ptithcm.util.invalidateBearerTokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrerequisiteRepository @Inject constructor(
    private val client: HttpClient
) : Cacheable {
    private val cachedPrerequisites =
        mutableMapOf<Pair<Int, Int>, CacheEntry<PrerequisiteResponse>>()
    private val cachedPrerequisitesType =
        mutableMapOf<Pair<Int, Int>, CacheEntry<List<PrerequisitesTypeResponse>>>()
    private val cacheTimeoutMillis = 5 * 60 * 1000L

    suspend fun getPrerequisites(
        limit: Int = 40,
        page: Int = 1,
        prerequisiteType: Int = 1,
        isForceRefresh: Boolean = false
    ): Result<PrerequisiteResponse> {
        val cacheKey = Pair(limit, page)
        val now = System.currentTimeMillis()
        val cached = cachedPrerequisites[cacheKey]

        if (isForceRefresh) {
            cachedPrerequisites.clear()
        } else if (cached != null && now - cached.timestamp < cacheTimeoutMillis) {
            // Cache hit and not expired
            return Result.success(cached.data)
        }

        return try {
            val res = this.client.post("https://uis.ptithcm.edu.vn/api/rms/w-locdsmontienquyet") {
                contentType(ContentType.Application.Json)
                setBody(
                    """
                    {
                      "loai_tien_quyet": $prerequisiteType,
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
            }.body<PrerequisiteResponse>()
            cachedPrerequisites[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPrerequisiteType(
        limit: Int = 100,
        page: Int = 1,
        isForceRefresh: Boolean = false
    ): Result<List<PrerequisitesTypeResponse>> {
        val cacheKey = Pair(limit, page)
        val now = System.currentTimeMillis()
        val cached = cachedPrerequisitesType[cacheKey]

        if (isForceRefresh) {
            cachedPrerequisitesType.clear()
        } else if (cached != null && now - cached.timestamp < cacheTimeoutMillis) {
            // Cache hit and not expired
            return Result.success(cached.data)
        }

        return try {
            val res =
                this.client.post("https://uis.ptithcm.edu.vn/api/rms/w-locdsloaitienquyet") {
                    contentType(ContentType.Application.Json)
                }.body<List<PrerequisitesTypeResponse>>()
            cachedPrerequisitesType[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearCache() {
        cachedPrerequisites.clear()
        cachedPrerequisitesType.clear()
        this.client.invalidateBearerTokens()
    }
}
