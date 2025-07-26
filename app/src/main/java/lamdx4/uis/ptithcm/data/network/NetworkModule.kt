package lamdx4.uis.ptithcm.data.network

import com.google.android.gms.auth.api.Auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.DEFAULT
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 5000
                socketTimeoutMillis = 10000
            }
            install(UserAgent) {
                agent = "UIS PTIT HCM Android/1.0"
            }
            install(HttpSend){

            }
            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 2)
                exponentialDelay()
            }
        }
    }
}