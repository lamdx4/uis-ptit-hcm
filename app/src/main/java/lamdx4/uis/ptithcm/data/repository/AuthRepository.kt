package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.* //  Ktor 2.x
import io.ktor.serialization.kotlinx.json.*         //  json()
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Singleton

@Singleton
class AuthRepository {
    // Singleton
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // <-- dòng này rất quan trọng!
            })
        }
    }

    suspend fun login(username: String, password: String): Result<String> {
        return try {
            val response: HttpResponse = client.post("http://uis.ptithcm.edu.vn/api/auth/login") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    listOf(
                        "username" to username,
                        "password" to password,
                        "grant_type" to "password"
                    ).formUrlEncode()
                )
            }
            val json = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val token = json["access_token"]?.jsonPrimitive?.contentOrNull
            if (token != null) Result.success(token)
            else Result.failure(Exception(json["message"]?.jsonPrimitive?.content ?: "Đăng nhập thất bại"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}