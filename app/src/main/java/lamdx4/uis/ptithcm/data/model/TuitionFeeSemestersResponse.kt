package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TuitionFeeSemestersResponse(
    @SerialName("data")
    val data: TuitionFeeSemestersData,

    @SerialName("result")
    val result: Boolean,

    @SerialName("code")
    val code: Int
)

@Serializable
data class TuitionFeeSemestersData(
    @SerialName("total_items")
    val totalItems: Int,

    @SerialName("total_pages")
    val totalPages: Int,

    @SerialName("hoc_ky")
    val semester: Int,

    @SerialName("hoc_ky_theo_ngay_hien_tai")
    val semesterByCurrentDate: Int,

    @SerialName("thoi_gian_hieu_luc_diem_danh_qrcode")
    val qrCodeAttendanceValidityTime: Int,

    @SerialName("is_an_in_thong_ke")
    val isHideStatisticsPrint: Boolean,

    @SerialName("ds_hoc_ky")
    val semesterList: List<TuitionFeeSemester>,

    @SerialName("hoc_ky_dang_ky")
    val registrationSemester: Int
)

@Serializable
data class TuitionFeeSemester(
    @SerialName("hoc_ky")
    val semesterCode: Int,

    @SerialName("ten_hoc_ky")
    val semesterName: String,

    @SerialName("is_cvht")
    val isClassAdvisor: Boolean,

    @SerialName("hiendiensv")
    val currentStudents: Int
)
