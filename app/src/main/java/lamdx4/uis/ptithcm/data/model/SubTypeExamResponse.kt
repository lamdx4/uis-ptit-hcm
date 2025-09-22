package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubTypeExamResponse(
    @SerialName("is_xuat_ds_thi_bo_mon") val isExportExamList: Boolean,
    @SerialName("is_in_bang_ghi_diem") val isPrintScoreSheet: Boolean,
    @SerialName("lich_thi_sinh_vien") val studentExamSchedule: StudentExamSchedule,
    val result: Boolean,
    val code: Int
)

@Serializable
data class StudentExamSchedule(
    val data: ExamScheduleData? = null,
    @SerialName("ds_field_an") val hiddenFields: List<HiddenField>,
    @SerialName("thong_bao_ghi_chu") val noteMessage: String?,
    val result: Boolean,
    val code: Int
)

@Serializable
data class ExamScheduleData(
    @SerialName("total_items") val totalItems: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("is_DHMO") val isDHMO: Boolean,
    @SerialName("ds_lich_thi") val examList: List<ExamItem>,
    @SerialName("thong_bao_no_hoc_phi") val tuitionDebtMessage: String?,
    @SerialName("is_show_in_sv_du_thi") val isShowInExamList: Boolean
)

@Serializable
data class ExamItem(
    @SerialName("id_nhom_thi") val groupId: String,
    @SerialName("id_mon_hoc") val subjectId: String,
    @SerialName("so_thu_tu") val order: Int,
    @SerialName("ky_thi") val examPeriod: String,
    @SerialName("dot_thi") val examBatch: String,
    @SerialName("ma_mon") val subjectCode: String,
    @SerialName("ten_mon") val subjectName: String,
    @SerialName("ten_mon_eg") val subjectNameEng: String? = null,
    @SerialName("ma_phong") val roomCode: String,
    @SerialName("ma_co_so") val campusCode: String,
    @SerialName("ngay_thi") val examDate: String,
    @SerialName("tiet_bat_dau") val startPeriod: String,
    @SerialName("so_tiet") val numberOfPeriods: String,
    @SerialName("gio_bat_dau") val startTime: String,
    @SerialName("so_phut") val durationMinutes: String,
    @SerialName("hinh_thuc_thi") val examFormat: String,
    @SerialName("ghi_chu_sv") val studentNote: String?,
    @SerialName("ghep_thi") val combinedExam: String,
    @SerialName("to_thi") val examGroup: String,
    @SerialName("ghep_phong") val combinedRoom: String? = null,
    @SerialName("ghi_chu_htt") val systemNote: String,
    @SerialName("si_so") val classSize: Int,
    @SerialName("dia_diem_thi") val examLocation: String
)
