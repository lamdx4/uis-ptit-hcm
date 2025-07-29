package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisteredScheduleResponse(
    @SerialName("data")
    val data: RegisteredScheduleData,
    @SerialName("result")
    val result: Boolean,
    @SerialName("code")
    val code: Int
)


@Serializable
data class RegisteredScheduleData(
    @SerialName("total_items")
    val totalItems: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("so_tin_chi_min")
    val minimumCredits: Int,
    @SerialName("ngay_in")
    val printedAt: String,
    @SerialName("is_show_nganh_hoc")
    val showMajor: Boolean,
    @SerialName("is_show_dia_diem_thi")
    val showExamLocation: Boolean,
    @SerialName("ds_kqdkmh")
    val registeredSubjects: List<RegisteredSubject>
)
@Serializable
data class RegisteredSubject(
    @SerialName("id_kqdk")
    val registrationId: String,
    @SerialName("trang_thai_mon")
    val subjectStatus: String,
    @SerialName("ngay_dang_ky")
    val registeredAt: String,
    @SerialName("nguoi_dang_ky")
    val registeredBy: String,
    @SerialName("is_da_rut_mon_hoc")
    val isWithdrawn: Boolean,
    @SerialName("enable_xoa")
    val canDelete: Boolean,
    @SerialName("dien_giai_enable_xoa")
    val deleteReason: String,
    @SerialName("hoc_phi_tam_tinh")
    val estimatedFee: Double,
    @SerialName("to_hoc")
    val subjectGroup: SubjectGroup,
    @SerialName("id_dia_diem_thi")
    val examLocationId: String,
    @SerialName("ten_dia_diem_thi")
    val examLocationName: String
)
@Serializable
data class SubjectGroup(
    @SerialName("id_to_hoc")
    val groupId: String,
    @SerialName("id_mon")
    val subjectId: String,
    @SerialName("ma_mon")
    val subjectCode: String,
    @SerialName("ten_mon")
    val subjectName: String,
    @SerialName("ten_mon_eg")
    val subjectNameEn: String? = null,
    @SerialName("so_tc")
    val creditsText: String,
    @SerialName("so_tc_so")
    val creditsNumber: Double,
    @SerialName("is_vuot")
    val isOverload: Boolean,
    @SerialName("nhom_to")
    val groupCode: String,
    @SerialName("lop")
    val classCode: String,
    @SerialName("is_kdk")
    val isNotRegistered: Boolean,
    @SerialName("sl_dk")
    val registeredCount: Int,
    @SerialName("sl_cp")
    val quota: Int,
    @SerialName("sl_cl")
    val remaining: Int,
    @SerialName("tkb")
    val schedule: String,
    @SerialName("is_hl")
    val isHighlight: Boolean,
    @SerialName("enable")
    val isEnabled: Boolean,
    @SerialName("hauk")
    val hauk: Boolean,
    @SerialName("is_dk")
    val isRegistered: Boolean,
    @SerialName("is_rot")
    val isFailed: Boolean,
    @SerialName("is_ctdt")
    val isCTDT: Boolean,
    @SerialName("is_chctdt")
    val isCHCTDT: Boolean,
    @SerialName("is_kg_lt")
    val isNonTheory: Boolean,
    @SerialName("thu")
    val dayOfWeek: Int,
    @SerialName("tbd")
    val startPeriod: Int,
    @SerialName("so_tiet")
    val numberOfPeriods: Int,
    @SerialName("is_kg_huy_kqdk")
    val isCancelDisabled: Boolean,
    @SerialName("is_kg_xet_trungtkb")
    val isConflictCheckDisabled: Boolean
)
