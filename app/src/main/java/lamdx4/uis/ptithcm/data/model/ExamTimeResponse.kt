package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExamTimeResponse(
    val data: ExamTimeData,
    val result: Boolean,
    val code: Int
)

@Serializable
data class ExamTimeData(
    @SerialName("total_items") val totalItems: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("hoc_ky") val currentSemester: Int,
    @SerialName("hoc_ky_theo_ngay_hien_tai") val semesterByCurrentDate: Int,
    @SerialName("thoi_gian_hieu_luc_diem_danh_qrcode") val qrCodeAttendanceValidity: Int,
    @SerialName("is_an_in_thong_ke") val isHideStatisticsPrint: Boolean,
    @SerialName("ds_hoc_ky") val semesters: List<ExamTime>,
    @SerialName("hoc_ky_dang_ky") val registeredSemester: Int
)

@Serializable
data class ExamTime(
    @SerialName("hoc_ky") val semesterCode: Int,
    @SerialName("ten_hoc_ky") val semesterName: String,
    @SerialName("is_cvht") val isCvht: Boolean,
    @SerialName("hiendiensv") val hienDienSv: Int,
    @SerialName("ngay_bat_dau_hk") val startDate: String,
    @SerialName("ngay_ket_thuc_hk") val endDate: String
)
