package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("userName") val userName: String,
    @SerialName("id") val id: String,
    @SerialName("logtime") val logTime: String,
    @SerialName("code") val code: String,
    @SerialName("result") val result: String,
    @SerialName("passtype") val passType: String,
    @SerialName("name") val name: String,
    @SerialName("principal") val principal: String,
    @SerialName("idpc") val idpc: String,
    @SerialName("roles") val roles: String,
    @SerialName("wcf") val wcf: String,
    @SerialName(".expires") val expiresAt: String,
    @SerialName(".issued") val issuedAt: String,
)
