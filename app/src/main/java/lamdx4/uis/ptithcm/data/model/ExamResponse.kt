package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExamResponse(
    val data: ExamData,
    @SerialName("ds_field_an") val hiddenFields: List<ExamHiddenField>,
    @SerialName("thong_bao_ghi_chu") val noteMessage: String,
    val result: Boolean,
    val code: Int
)

@Serializable
data class ExamData(
    @SerialName("total_items") val totalItems: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("is_DHMO") val isDHMO: Boolean,
    @SerialName("ds_lich_thi") val examSchedules: List<Exam>,
    @SerialName("ds_lich_hoan_thi") val makeUpSchedules: List<String>,
    @SerialName("title_lich_hoan_thi") val makeUpScheduleTitle: String,
    @SerialName("thong_bao_no_hoc_phi") val tuitionNotice: String,
    @SerialName("is_show_in_sv_du_thi") val isShowStudentExam: Boolean
)

@Serializable
data class Exam(
    @SerialName("id_nhom_thi") val groupId: String,
    @SerialName("id_mon_hoc") val subjectId: String,
    @SerialName("id_kqdk") val registrationResultId: String,
    @SerialName("so_thu_tu") val order: Int,
    @SerialName("ky_thi") val examType: String,
    @SerialName("dot_thi") val examBatch: String,
    @SerialName("ma_mon") val subjectCode: String,
    @SerialName("ten_mon") val subjectName: String,
    @SerialName("ten_mon_eg") val subjectNameEg: String,
    @SerialName("ma_phong") val roomCode: String,
    @SerialName("ma_co_so") val campusCode: String,
    @SerialName("ngay_thi") val examDate: String,
    @SerialName("tiet_bat_dau") val startPeriod: String,
    @SerialName("so_tiet") val periods: String,
    @SerialName("gio_bat_dau") val startTime: String,
    @SerialName("so_phut") val durationMinutes: String,
    @SerialName("hinh_thuc_thi") val examFormat: String,
    @SerialName("ghi_chu_sv") val studentNote: String,
    @SerialName("ghep_thi") val combinedExam: String,
    @SerialName("to_thi") val examGroup: String,
    @SerialName("nhom_thi") val groupName: String,
    @SerialName("ghep_phong") val combinedRoom: String,
    @SerialName("ghi_chu_htt") val systemNote: String,
    @SerialName("ghi_chu_du_thi") val extraNote: String,
    @SerialName("cam_thi") val bannedExam: String,
    @SerialName("si_so") val numberOfStudents: Int,
    @SerialName("xem_dssv") val viewStudentList: String,
    @SerialName("dia_diem_thi") val examLocation: String,
    @SerialName("ds_diem_mon_hoc") val subjectScores: List<SubjectScore>
)

@Serializable
data class SubjectScore(
    @SerialName("ten_mon") val subjectName: String,
    @SerialName("mon_hoc_nganh") val isMajorSubject: Boolean,
    @SerialName("ket_qua") val result: Int,
    @SerialName("hien_thi_ket_qua") val showResult: Boolean,
    @SerialName("KhoaThi") val examDepartment: Int,
    @SerialName("khong_tinh_diem_tbtl") val excludeAverage: Int,
    @SerialName("ds_diem_thanh_phan") val componentScores: List<String>
)

@Serializable
data class ExamHiddenField(
    @SerialName("ten_field") val fieldName: String,
    val enable: Boolean
)
