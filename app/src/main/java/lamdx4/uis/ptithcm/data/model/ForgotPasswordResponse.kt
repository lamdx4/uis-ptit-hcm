package lamdx4.uis.ptithcm.data.model

@kotlinx.serialization.Serializable
data class ForgotPasswordResponse(
    val result: Boolean,
    val message: String = "",
    val code: Int
)