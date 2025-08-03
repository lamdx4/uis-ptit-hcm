package lamdx4.uis.ptithcm.util

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

fun HttpClient.invalidateBearerTokens() {
    authProvider<BearerAuthProvider>()?.clearToken()
}
