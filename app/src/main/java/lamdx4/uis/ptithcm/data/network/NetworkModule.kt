package lamdx4.uis.ptithcm.data.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.formUrlEncode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import lamdx4.uis.ptithcm.data.local.LoginPrefs
import lamdx4.uis.ptithcm.data.model.LoginResponse
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(
        loginPrefs: LoginPrefs // Inject LoginPrefs vào đây
    ): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }

            install(Logging) {
                logger = Logger.SIMPLE // hoặc Logger.DEFAULT
                level = LogLevel.ALL   // Ghi log toàn bộ: URL, headers, body…
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 5000
                socketTimeoutMillis = 10000
            }

            install(UserAgent) {
                agent = "UIS PTIT HCM Android/1.0"
            }

            // Gắn access token vào tất cả request
            install(Auth) {
                bearer {
                    // Load token before request (must be suspend)
                    loadTokens {
                        val token = loginPrefs.getAccessToken()
                        val refreshToken = loginPrefs.getRefreshToken()
                        BearerTokens(token ?: "", refreshToken ?: "")
                    }
                    refreshTokens {
                        val currentRefreshToken = loginPrefs.getRefreshToken()
                        if (currentRefreshToken.isNullOrBlank()) return@refreshTokens null

                        val response = client.post("http://uis.ptithcm.edu.vn/api/auth/login") {
                            header("Content-Type", "application/x-www-form-urlencoded")
                            setBody(
                                listOf(
                                    "grant_type" to "refresh_token",
                                    "refresh_token" to currentRefreshToken
                                ).formUrlEncode()
                            )
                        }

                        val tokenResponse = response.body<LoginResponse>()
                        loginPrefs.saveAccessToken(tokenResponse.accessToken)
                        BearerTokens(tokenResponse.accessToken, currentRefreshToken)
                    }
                }
            }
            install(HttpCookies){
                storage = AcceptAllCookiesStorage()
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 2)
                exponentialDelay()
            }
        }
    }
}
