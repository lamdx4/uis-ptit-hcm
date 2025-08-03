package lamdx4.uis.ptithcm.data.model

@kotlinx.serialization.Serializable
data class ResetPasswordRequest(
    val username: String,
    val password: String, // tức là OTP
    val newpass: String
)

@kotlinx.serialization.Serializable
data class ForgotPasswordRequest(
    val username: String,
    val principal: String,
    val type: Int
)
