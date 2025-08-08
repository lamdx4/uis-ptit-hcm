package lamdx4.uis.ptithcm.data.repository

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import lamdx4.uis.ptithcm.data.model.ForgotPasswordRequest
import lamdx4.uis.ptithcm.data.model.ForgotPasswordResponse
import lamdx4.uis.ptithcm.data.model.Login2Response
import lamdx4.uis.ptithcm.data.model.LoginResponse
import lamdx4.uis.ptithcm.data.model.ResetPasswordRequest
import lamdx4.uis.ptithcm.util.invalidateBearerTokens
import java.net.URLDecoder
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val client: HttpClient
) : Cacheable {

    companion object {
        const val TYPE_LOGIN = "SSO"
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

    suspend fun login2(
        username: String,
        password: String,
        uri: String = "https://uis.ptithcm.edu.vn/#/home"
    ): Result<Login2Response> {
        return try {
            val jsonString = """{"username":"$username","password":"$password","uri":"$uri"}"""
            val base64Code = Base64.getEncoder().encodeToString(jsonString.toByteArray())
            val response: HttpResponse = client.get("https://uis.ptithcm.edu.vn/api/pn-signin") {
                url {
                    parameters.append("code", base64Code)
                    parameters.append("gopage", "")
                    parameters.append("mgr", "1")
                }
            }
            val finalUrl = response.request.url.toString()

            val currUserBase64 = URLDecoder.decode(
                finalUrl.substringAfter("CurrUser=").substringBefore("&"),
                "UTF-8"
            )



            val errorMessage =
                finalUrl.substringAfter("error=", "").takeIf { "error=" in finalUrl }?.let {
                    URLDecoder.decode(it, "UTF-8")
                }

            this.client.invalidateBearerTokens()

            if (currUserBase64 == "null" || currUserBase64.isBlank()) {
                Result.failure(
                    Exception(
                        errorMessage
                    )
                )
            } else {
                val userJson = String(Base64.getDecoder().decode(currUserBase64))
                val json = Json {
                    ignoreUnknownKeys = true
                }
                Result.success(
                    json.decodeFromString<Login2Response>(userJson)
                )
            }

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