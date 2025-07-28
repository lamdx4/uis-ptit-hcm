package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RegisterScheduleResponse(
    @SerialName("loai_hien_thi_tuan")
    val weekDisplayType: Int,
    @SerialName("is_merge_to_hoc")
    val isMergeClass: Boolean,
    @SerialName("data")
    val data: RegisterScheduleData,
    @SerialName("is_show_thoi_gian")
    val isShowTime: Boolean,
    @SerialName("is_kg_cap_nhat_ssdk_khi_xoa")
    val isNoUpdateRegisterStateOnDelete: Boolean,
    @SerialName("is_in_so_bao_giang")
    val isPrintTeachingNotice: Boolean,
    @SerialName("result")
    val result: Boolean,
    @SerialName("code")
    val code: Int,
    @SerialName("time")
    val time: String
)

@Serializable
data class RegisterScheduleData(
    @SerialName("total_items")
    val totalItems: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("dien_giai_enable_chung")
    val generalEnableExplanation: String,
    @SerialName("ghi_chu_dkmh")
    val registrationNote: String,
    @SerialName("trong_thoi_gian_dang_ky")
    val isInRegistrationTime: Boolean,
    @SerialName("trong_thoi_gian_duyet_kqdk")
    val isInApproveTime: Boolean,
    @SerialName("hien_cot_tach_phieu_nop_tien")
    val showSplitFeeColumn: Boolean,
    @SerialName("addin_duyet_kqdk")
    val allowApproveAddin: Boolean,
    @SerialName("hien_cot_hoc_phi")
    val showTuitionColumn: Boolean,
    @SerialName("hien_cot_ma_lop")
    val showClassCodeColumn: Boolean,
    @SerialName("hien_cot_so_luong")
    val showQuantityColumn: Boolean,
    @SerialName("hien_thi_cot_lich_thi")
    val showExamColumn: Boolean,
    @SerialName("hoc_ky_dang_ky")
    val registrationSemester: String,
    @SerialName("is_show_tietbd")
    val isShowStartPeriod: Boolean,
    @SerialName("is_merge_dong_tkbhk")
    val isMergeScheduleRow: Boolean,
    @SerialName("ds_khoa")
    val faculties: List<SimpleObject>,
    @SerialName("ds_lop")
    val classes: List<SimpleObject>,
    @SerialName("ds_mon_hoc")
    val subjects: List<Subject>,
    @SerialName("ds_nhom_to")
    val groups: List<RegisterGroup>,
    @SerialName("hien_thi_cot_mon_hoc_bd")
    val showPrerequisiteColumn: Boolean,
    @SerialName("is_xem_danh_sach_sinh_vien")
    val canViewStudentList: Boolean
)

@Serializable
data class SimpleObject(
    @SerialName("ma")
    val code: String,
    @SerialName("ten")
    val name: String
)

@Serializable
data class Subject(
    @SerialName("ma")
    val code: String,
    @SerialName("ten")
    val name: String,
    @SerialName("ten_eg")
    val englishName: String? = null
)

@Serializable
data class RegisterGroup(
    @SerialName("id_to_hoc")
    val groupId: String,
    @SerialName("id_mon")
    val subjectId: String,
    @SerialName("ma_mon")
    val subjectCode: String,
    @SerialName("ten_mon")
    val subjectName: String,
    @SerialName("ten_mon_eg")
    val subjectEnglishName: String? = null,
    @SerialName("so_tc")
    val credit: String,
    @SerialName("so_tc_so")
    val creditNumber: Double,
    @SerialName("is_vuot")
    val isOverload: Boolean,
    @SerialName("nhom_to")
    val groupName: String,
    @SerialName("to")
    val subGroup: String,
    @SerialName("lop")
    val className: String,
    @SerialName("ds_lop")
    val classList: List<String>,
    @SerialName("ds_khoa")
    val facultyList: List<String>,
    @SerialName("is_kdk")
    val isNotRegisteredYet: Boolean,
    @SerialName("sl_dk")
    val registeredCount: Int,
    @SerialName("sl_cp")
    val capacity: Int,
    @SerialName("sl_cl")
    val remaining: Int,
    @SerialName("tkb")
    val schedule: String,
    @SerialName("is_hl")
    val isHighlight: Boolean,
    @SerialName("enable")
    val isEnabled: Boolean,
    @SerialName("hauk")
    val isAfterDeadline: Boolean,
    @SerialName("is_dk")
    val isRegistered: Boolean,
    @SerialName("gc_enable")
    val enableReason: String,
    @SerialName("is_rot")
    val isRepeat: Boolean,
    @SerialName("is_ctdt")
    val isCurriculumSubject: Boolean,
    @SerialName("is_chctdt")
    val isCurriculumRequirement: Boolean,
    @SerialName("is_kg_lt")
    val isNotTheory: Boolean,
    @SerialName("thu")
    val dayOfWeek: Int,
    @SerialName("tbd")
    val startPeriod: Int,
    @SerialName("so_tiet")
    val numberOfPeriods: Int,
    @SerialName("is_kg_huy_kqdk")
    val isNotAllowCancel: Boolean,
    @SerialName("is_kg_xet_trungtkb")
    val isNotCheckConflict: Boolean
)
