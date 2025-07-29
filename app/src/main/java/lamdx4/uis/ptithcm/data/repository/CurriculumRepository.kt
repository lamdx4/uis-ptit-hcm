package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import lamdx4.uis.ptithcm.data.model.CurriculumResponse
import lamdx4.uis.ptithcm.data.model.CurriculumTypeResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurriculumRepository @Inject constructor(
    private val client : HttpClient
){
    suspend fun getCurriculum(programType: Int): CurriculumResponse {
        return this.client.post("http://uis.ptithcm.edu.vn/api/sch/w-locdsctdtsinhvien") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)

            setBody("""
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
            """.trimIndent())
        }.body<CurriculumResponse>()
    }

    suspend fun getCurriculumTypes(): List<CurriculumTypeResponse> {
        return this.client.post("http://uis.ptithcm.edu.vn/api/sch/w-locdschuongtrinhdaotao") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }.body<List<CurriculumTypeResponse>>()
    }
}