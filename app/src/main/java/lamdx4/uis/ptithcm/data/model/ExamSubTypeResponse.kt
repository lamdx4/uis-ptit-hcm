package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExamSubTypeResponse(
    val data: ExamSubTypeData,
    val result: Boolean,
    val code: Int
)

@Serializable
data class ExamSubTypeData(
    @SerialName("total_items") val totalItems: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("ds_du_lieu") val dataItems: List<ExamSubType>
)

@Serializable
data class ExamSubType(
    @SerialName("id_du_lieu") val dataId: String,
    @SerialName("ma_du_lieu") val dataCode: String,
    @SerialName("ten_du_lieu") val dataName: String,
    @SerialName("ten_du_lieu_eg") val dataNameEng: String? = null
)
