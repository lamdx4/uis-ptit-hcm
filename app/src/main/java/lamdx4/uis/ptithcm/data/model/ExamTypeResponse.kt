package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExamTypeResponse(
    val data: ExamTypeData,
    val result: Boolean,
    val code: Int
)

@Serializable
data class ExamTypeData(
    @SerialName("total_items") val totalItems: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("ds_doi_tuong_tkb") val scheduleObjects: List<ExamTypeObject>
)

@Serializable
data class ExamTypeObject(
    @SerialName("loai_doi_tuong") val objectType: Int,
    @SerialName("ten_doi_tuong") val objectName: String
)
