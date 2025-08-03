package lamdx4.uis.ptithcm.data.repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import lamdx4.uis.ptithcm.data.model.RegisterScheduleResponse
import lamdx4.uis.ptithcm.data.model.RegisteredScheduleResponse
import lamdx4.uis.ptithcm.data.model.SubjectFilter
import lamdx4.uis.ptithcm.util.invalidateBearerTokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRegistrationRepository @Inject constructor(
    private val client: HttpClient
) : Cacheable {
    suspend fun getAllSubjects(): Result<RegisterScheduleResponse> {
        val data = """{
            "is_CVHT": false,
            "additional": {
                "paging": {
                    "limit": 99999,
                    "page": 1
                },
                "ordering": [
                    {
                        "name": "",
                        "order_type": ""
                    }
                ]
            }
        }"""
        return try {
            val res = client.post(
                "https://uis.ptithcm.edu.vn/api/dkmh/w-locdsnhomto"
            ) {
                contentType(ContentType.Application.Json)
                setBody(data)
            }.body<RegisterScheduleResponse>()
            Result.success(res)
        } catch (e: Exception) {
            Log.e("CourseRegistrationRepository", e.message.toString())
            Result.failure(e)
        }
    }

    suspend fun getAllRegisteredSubject(): Result<RegisteredScheduleResponse> {
        val jsonData = """{"is_CVHT":false,"is_Clear":false}"""
        return try {
            val res =this.client.post("https://uis.ptithcm.edu.vn/api/dkmh/w-locdskqdkmhsinhvien") {
                contentType(ContentType.Application.Json)
                setBody(jsonData)
            }.body<RegisteredScheduleResponse>()
            Result.success(res)
        }
        catch (e : Exception){
            Log.e("CourseRegistrationRepository", e.message.toString())
            Result.failure(e)
        }
    }

    suspend fun getAllFilter(): Result<List<SubjectFilter>> {
        return try {
            val res = this.client.post(
                "https://uis.ptithcm.edu.vn/api/dkmh/w-locdsdieukienloc"
            ).body<List<SubjectFilter>>()
            Result.success(res)
        } catch (e: Exception) {
            Log.e("CourseRegistrationRepository", e.message.toString())
            Result.failure(e)
        }
    }

    override fun clearCache() {
        this.client.invalidateBearerTokens()
    }
}