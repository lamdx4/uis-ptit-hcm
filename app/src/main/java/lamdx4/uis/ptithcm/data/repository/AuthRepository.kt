package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import lamdx4.uis.ptithcm.data.model.ForgotPasswordRequest
import lamdx4.uis.ptithcm.data.model.ForgotPasswordResponse
import lamdx4.uis.ptithcm.data.model.LoginResponse
import lamdx4.uis.ptithcm.data.model.ResetPasswordRequest
import lamdx4.uis.ptithcm.util.invalidateBearerTokens
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val client: HttpClient
) : Cacheable {

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

    suspend fun forgotPassword(studentId: String, email: String): Result<String> {
        return try {
            val type = 0
            val response: HttpResponse =
                client.post("https://uis.ptithcm.edu.vn/api/auth/forget-password") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        ForgotPasswordRequest(
                            username = studentId,
                            principal = email,
                            type = type
                        )
                    )
                }
            val data = response.body<ForgotPasswordResponse>()
            if (data.code == 200) {
                Result.success("OTP đã được gửi thành công.")
            } else {
                Result.failure(Exception(data.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(
        studentId: String,
        otp: String,
        newPassword: String
    ): Result<String> {
        return try {
            val response = client.post("https://uis.ptithcm.edu.vn/api/auth/selfreset-password") {
                contentType(ContentType.Application.Json)
                setBody(
                    ResetPasswordRequest(
                        username = studentId,
                        password = otp,
                        newpass = newPassword
                    )
                )
            }
            val data = response.body<ForgotPasswordResponse>()
            if (data.code == 200) {
                Result.success("Đặt lại mật khẩu thành công.")
            } else {
                Result.failure(Exception(data.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }


    override fun clearCache() {
        this.client.invalidateBearerTokens()
    }
}