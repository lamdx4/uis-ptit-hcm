package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import lamdx4.uis.ptithcm.data.model.CurriculumResponse
import lamdx4.uis.ptithcm.data.model.CurriculumTypeResponse
import lamdx4.uis.ptithcm.util.invalidateBearerTokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurriculumRepository @Inject constructor(
    private val client : HttpClient
) : Cacheable  {
    private var cachedCurriculumTypes: List<CurriculumTypeResponse>? = null
    private val cachedCurriculums = mutableMapOf<Int, CurriculumResponse>()

    suspend fun getCurriculum(programType: Int, forceRefresh: Boolean = false): Result<CurriculumResponse> {
        if (!forceRefresh && cachedCurriculums.containsKey(programType)) {
            return Result.success(cachedCurriculums[programType]!!)
        }

        return try {
            val response: HttpResponse = client.post("https://uis.ptithcm.edu.vn/api/sch/w-locdsctdtsinhvien") {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                setBody(
                    """
                {
                  "filter": {
                    "loai_chuong_trinh_dao_tao": $programType
                  },
                  "additional": {
                    "paging": {
                      "limit": 500,
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
            }

            if (response.status.isSuccess()) {
                val curriculum = response.body<CurriculumResponse>()
                cachedCurriculums[programType] = curriculum // Lưu cache
                Result.success(response.body())
            } else {
                val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
                val errorMessage = json["message"]?.jsonPrimitive?.content ?: "Lỗi tải chương trình đào tạo"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurriculumTypes(forceRefresh: Boolean = false): Result<List<CurriculumTypeResponse>> {
        if (!forceRefresh && !cachedCurriculumTypes.isNullOrEmpty()) {
            return Result.success(cachedCurriculumTypes!!)
        }

        return try {
            val response: HttpResponse = client.post("https://uis.ptithcm.edu.vn/api/sch/w-locdschuongtrinhdaotao") {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }

            if (response.status.isSuccess()) {
                val types = response.body<List<CurriculumTypeResponse>>()
                cachedCurriculumTypes = types
                Result.success(response.body())
            } else {
                val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
                val errorMessage = json["message"]?.jsonPrimitive?.content ?: "Lỗi tải loại chương trình đào tạo"
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearCache() {
        this.client.invalidateBearerTokens()
        cachedCurriculumTypes = null
        cachedCurriculums.clear()
    }
}
