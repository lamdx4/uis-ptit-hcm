package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EducationProgramResponse(
    val data: EducationProgramData,
    val result: Boolean,
    val code: Int
)

@Serializable
data class EducationProgramData(
    @SerialName("total_items") val totalItems: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("is_xem_tai_lieu") val isViewDocument: Boolean,
    @SerialName("is_show_nhomtc") val isShowCreditGroup: Boolean,
    @SerialName("is_view_sodo") val isViewDiagram: Boolean,
    @SerialName("tong_sotc_ctdt") val totalCreditsProgram: Int,
    @SerialName("tong_sotctl") val totalCredits: Int,
    @SerialName("message_tong_tiet") val totalPeriodsMessage: String,
    @SerialName("is_show_tiettp") val isShowTrainingPeriods: Boolean,
    @SerialName("so_tc_phai_dat") val requiredCredits: Int,
    @SerialName("url_hoc_lieu_chi_tiet") val detailMaterialUrl: String,
    @SerialName("bearer_token") val bearerToken: String,
    @SerialName("ds_nganh_sinh_vien") val studentMajors: List<StudentMajor>,
    @SerialName("ds_chuyen_nganh_sv") val studentSpecializations: List<String>,
    @SerialName("ds_CTDT_hocky") val semesterPrograms: List<SemesterProgram>,
    @SerialName("ds_CTDT_hocky_n2") val semesterProgramsN2: List<String>,
    @SerialName("ds_field_an") val hiddenFields: List<HiddenField>,
    @SerialName("ds_ctdt_tchuan") val standardPrograms: List<String>
)

@Serializable
data class StudentMajor(
    @SerialName("loai_nganh") val majorType: Int,
    @SerialName("ma_nganh") val majorCode: String,
    @SerialName("ten_nganh") val majorName: String
)

@Serializable
data class SemesterProgram(
    @SerialName("hoc_ky") val semesterCode: String,
    @SerialName("ten_hoc_ky") val semesterName: String,
    @SerialName("ds_CTDT_mon_hoc") val courses: List<Course>
)

@Serializable
data class Course(
    @SerialName("id_khoi") val blockId: String,
    @SerialName("id_mon") val courseId: String,
    @SerialName("ma_mon") val courseCode: String,
    @SerialName("ten_mon") val courseName: String,
    @SerialName("ten_mon_eg") val courseNameEg: String,
    @SerialName("so_tin_chi") val credit: String,
    @SerialName("so_tin_chi_hp") val tuitionCredit: String,
    @SerialName("mon_bat_buoc") val requiredCourse: String,
    @SerialName("nhom_tc") val creditGroup: String,
    @SerialName("ma_chnganh") val specializationCode: String,
    @SerialName("ten_chnganh") val specializationName: String,
    @SerialName("mon_da_hoc") val completedCourse: String,
    @SerialName("ly_thuyet") val theoryHours: String,
    @SerialName("thuc_hanh") val practiceHours: String,
    @SerialName("tong_tiet") val totalHours: String,
    @SerialName("so_tc_min") val minCredits: String,
    @SerialName("so_tc_max") val maxCredits: String,
    @SerialName("mon_da_dat") val passedCourse: String,
    @SerialName("ghi_chu_mon_hoc") val courseNote: String,
    @SerialName("mon_cot_loi") val coreCourse: String,
    @SerialName("ds_tiet_thanh_phan") val sessionComponents: List<SessionComponent>
)

@Serializable
data class SessionComponent(
    @SerialName("ten_thanh_phan") val componentName: String,
    @SerialName("so_tiet") val sessionHours: String
)

@Serializable
data class HiddenField(
    @SerialName("ten_field") val fieldName: String,
    val enable: Boolean
)
