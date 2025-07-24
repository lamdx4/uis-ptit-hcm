package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import lamdx4.uis.ptithcm.data.model.EducationProgramResponse
import lamdx4.uis.ptithcm.data.model.EducationProgramType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurriculumRepository @Inject constructor() {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.INFO // Giảm từ BODY để không spam quá nhiều log
        }
    }

    suspend fun getEducationPrograms(accessToken: String, programType: Int): EducationProgramResponse {
        return this.client.post("http://uis.ptithcm.edu.vn/api/sch/w-locdsctdtsinhvien") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            header(HttpHeaders.ContentType, ContentType.Application.Json)

            setBody("""
                {
                  "filter": {
                    "loai_chuong_trinh_dao_tao": ${programType},
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
            """.trimIndent())
        }.body<EducationProgramResponse>()
    }

    suspend fun getEducationProgramTypes(accessToken: String): List<EducationProgramType> {
        return this.client.post("http://uis.ptithcm.edu.vn/api/sch/w-locdschuongtrinhdaotao") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }.body<List<EducationProgramType>>()
    }
}