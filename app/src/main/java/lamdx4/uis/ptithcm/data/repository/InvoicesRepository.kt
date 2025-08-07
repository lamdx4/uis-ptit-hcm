package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import lamdx4.uis.ptithcm.data.model.InvoiceAdditional
import lamdx4.uis.ptithcm.data.model.InvoiceFilter
import lamdx4.uis.ptithcm.data.model.InvoiceOrdering
import lamdx4.uis.ptithcm.data.model.InvoicePaging
import lamdx4.uis.ptithcm.data.model.InvoiceRequest
import lamdx4.uis.ptithcm.data.model.InvoiceResponse
import lamdx4.uis.ptithcm.util.CacheEntry
import lamdx4.uis.ptithcm.util.invalidateBearerTokens
import javax.inject.Inject

class InvoicesRepository @Inject constructor(
    private val client: HttpClient
) : Cacheable {
    // Cache map: key = Pair(limit, page)
    private val cachedInvoices = mutableMapOf<Pair<Int, Int>, CacheEntry<InvoiceResponse>>()

    // Cache timeout in milliseconds (e.g. 5 minutes)
    private val cacheTimeoutMillis = 5 * 60 * 1000L

    suspend fun getInvoices(limit: Int = 10, page: Int = 1): Result<InvoiceResponse> {
        val cacheKey = Pair(limit, page)
        val now = System.currentTimeMillis()
        val cached = cachedInvoices[cacheKey]

        if (cached != null && now - cached.timestamp < cacheTimeoutMillis) {
            // Cache hit and not expired
            return Result.success(cached.data)
        }

        // Cache miss or expired, fetch from API
        return try {
            val res = this.client.post("http://uis.ptithcm.edu.vn/api/rms/w-locdshoadondientu") {
                contentType(ContentType.Application.Json)
                setBody(
                    InvoiceRequest(
                        filter = InvoiceFilter(
                            semester = 0, keyword = ""
                        ),
                        additional = InvoiceAdditional(
                            paging = InvoicePaging(
                                limit = limit, page = page
                            ),
                            ordering = listOf(
                                InvoiceOrdering(
                                    name = "hoc_ky",
                                    orderType = 1
                                ),
                                InvoiceOrdering(
                                    name = "so_hoa_don",
                                    orderType = 0
                                )
                            )
                        )
                    )
                )
            }.body<InvoiceResponse>()
            cachedInvoices[cacheKey] = CacheEntry(res, now)
            Result.success(res)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearCache() {
        cachedInvoices.clear()
        this.client.invalidateBearerTokens()
    }
}