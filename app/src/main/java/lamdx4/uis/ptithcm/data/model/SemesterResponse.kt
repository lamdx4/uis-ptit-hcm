package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SemesterResponse(
    @SerialName("data")
    val data: SemesterData,
    @SerialName("result")
    val result: Boolean,
    @SerialName("code")
    val code: Int
)

@Serializable
data class SemesterData(
    @SerialName("total_items")
    val totalItems: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("hoc_ky")
    val currentSemester: Int,
    @SerialName("hoc_ky_theo_ngay_hien_tai")
    val currentSemesterByDate: Int,
    @SerialName("ds_hoc_ky")
    val semesters: List<Semester>
)

@Serializable
data class Semester(
    @SerialName("hoc_ky")
    val semesterCode: Int,
    @SerialName("ten_hoc_ky")
    val semesterName: String,
    @SerialName("hiendiensv")
    val isCurrentForStudent: Int,
    @SerialName("ngay_bat_dau_hk")
    val startDate: String,
    @SerialName("ngay_ket_thuc_hk")
    val endDate: String
) {
    val displayName: String
        get() = semesterName
}
