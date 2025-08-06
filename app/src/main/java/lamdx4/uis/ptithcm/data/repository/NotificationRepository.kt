package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import lamdx4.uis.ptithcm.data.model.Additional
import lamdx4.uis.ptithcm.data.model.Filter
import lamdx4.uis.ptithcm.data.model.NotificationRequest
import lamdx4.uis.ptithcm.data.model.NotificationResponse
import lamdx4.uis.ptithcm.data.model.Ordering
import lamdx4.uis.ptithcm.data.model.Paging
import lamdx4.uis.ptithcm.util.CacheEntry
import lamdx4.uis.ptithcm.util.invalidateBearerTokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val client: HttpClient
) : Cacheable {

    // Cache map: key = Pair(limit, page)
    private val cache = mutableMapOf<Pair<Int, Int>, CacheEntry<NotificationResponse>>()

    // Cache timeout in milliseconds (e.g. 5 minutes)
    private val cacheTimeoutMillis = 5 * 60 * 1000L

    suspend fun getNotification(limit: Int, page: Int): Result<NotificationResponse> {
        val cacheKey = Pair(limit, page)
        val now = System.currentTimeMillis()
        val cached = cache[cacheKey]

        if (cached != null && now - cached.timestamp < cacheTimeoutMillis) {
            // Cache hit and not expired
            return Result.success(cached.data)
        }

        // Cache miss or expired, fetch from API
        return try {
            val res = this.client.post("https://uis.ptithcm.edu.vn/api/web/w-locdsthongbao") {
                contentType(ContentType.Application.Json)
                setBody(
                    NotificationRequest(
                        filter = Filter(
                            null, hasContent = true, isWeb = true
                        ), additional = Additional(
                            paging = Paging(
                                limit = limit, page = page
                            ), ordering = listOf(
                                Ordering(
                                    name = "ngay_gui", orderType = 1
                                )
                            )
                        )
                    )
                )
            }.body<NotificationResponse>()
            // Save to cache
            cache[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readNotification(id: String): Result<Boolean> {
        // Khi đọc, có thể xóa cache nếu muốn (cache invalidation)
        return try {
            val res = this.client.post("https://uis.ptithcm.edu.vn/api/web/w-luudadocthongbao") {
                contentType(ContentType.Application.Json)
                setBody(
                    NotificationRequest(
                        filter = Filter(
                            id, hasContent = true, isWeb = true
                        ), additional = Additional(
                            paging = Paging(
                                limit = 100, page = 1
                            ), ordering = listOf(
                                Ordering(
                                    name = "ngay_gui", orderType = 1
                                )
                            )
                        )
                    )
                )
            }
            cache.clear()
            Result.success(res.status.value == 200)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearCache() {
        cache.clear()
        this.client.invalidateBearerTokens()
    }
}