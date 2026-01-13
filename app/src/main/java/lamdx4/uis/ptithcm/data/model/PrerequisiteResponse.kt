package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrerequisiteResponse(
    val data: PrerequisiteData,
    val result: Boolean,
    val code: Int
)

@Serializable
data class PrerequisiteData(
    @SerialName("total_items") val totalItems: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("ds_mon_tien_quyet") val prerequisites: List<PrerequisiteSubject>
)

@Serializable
data class PrerequisiteSubject(
    @SerialName("ma_mon_dang_ky") val subjectCode: String,
    @SerialName("ten_mon_dang_ky") val subjectName: String,
    @SerialName("ma_mon_yeu_cau") val prerequisiteCode: String,
    @SerialName("ten_mon_yeu_cau") val prerequisiteName: String
)
