package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EducationProgramType(
    @SerialName("gia_tri") val value: Int,
    @SerialName("mieu_ta") val description: String,
    @SerialName("is_mac_dinh") val isDefault: Boolean
)
