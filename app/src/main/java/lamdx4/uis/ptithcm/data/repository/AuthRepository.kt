package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import lamdx4.uis.ptithcm.data.model.LoginResponse
import lamdx4.uis.ptithcm.util.invalidateBearerTokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() : Cacheable {
    // Singleton
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // <-- dòng này rất quan trọng!
            })
        }
    }

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response: HttpResponse = client.post("https://uis.ptithcm.edu.vn/api/auth/login") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    listOf(
                        "username" to username, "password" to password, "grant_type" to "password"
                    ).formUrlEncode()
                )
            }
            val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val token = json["access_token"]?.jsonPrimitive?.contentOrNull
            if (token != null) Result.success(response.body<LoginResponse>())
            else Result.failure(
                Exception(
                    json["message"]?.jsonPrimitive?.content ?: "Đăng nhập thất bại"
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun clearCache() {
        this.client.invalidateBearerTokens()
    }
}