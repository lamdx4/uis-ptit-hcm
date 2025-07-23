package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response from API w-locdsdiemsinhvien
 */
@Serializable
data class GradeResponse(
    @SerialName("data")
    val data: GradeData,
    @SerialName("result")
    val result: Boolean,
    @SerialName("code")
    val code: Int
)

@Serializable
data class GradeData(
    @SerialName("total_items")
    val totalItems: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("is_kkbd")
    val isKkbd: Boolean,
    @SerialName("ds_diem_hocky")
    val semesterGrades: List<SemesterGrade>,
    @SerialName("ds_field_an_cot_diem")
    val hiddenGradeFields: List<String>,
    @SerialName("hien_thi_khoa_thi")
    val showExamBlock: Boolean,
    @SerialName("hien_thi_cot_diem_tp")
    val showComponentGrades: Boolean,
    @SerialName("an_chi_tiet_diem_tp")
    val hideComponentDetails: Boolean,
    @SerialName("hien_thi_cot_diem_k1")
    val showMidtermGrades: Boolean,
    @SerialName("hien_thi_cot_mhtt")
    val showSubjectCode: Boolean,
    @SerialName("hien_thi_cot_stctt")
    val showCreditCode: Boolean,
    @SerialName("hien_thi_cot_diemtk10")
    val showGrade10: Boolean,
    @SerialName("hien_thi_cot_diemtk4")
    val showGrade4: Boolean,
    @SerialName("hien_thi_cot_diem_thi")
    val showExamGrade: Boolean,
    @SerialName("hien_thi_cot_mh_nganh")
    val showMajorSubject: Boolean,
    @SerialName("hien_thi_cot_hk_chuyen_diem")
    val showTransferCredit: Boolean,
    @SerialName("mesage_diemtk4")
    val messageGrade4: String,
    @SerialName("mesage_diemtkc")
    val messageGradeC: String,
    @SerialName("mesage_diemtk10")
    val messageGrade10: String,
    @SerialName("mesage_diemk1")
    val messageMidterm: String
)

@Serializable
data class SemesterGrade(
    @SerialName("loai_nganh")
    val majorType: Int? = null,
    @SerialName("hoc_ky")
    val semesterCode: String,
    @SerialName("ten_hoc_ky")
    val semesterName: String,
    @SerialName("dtb_hk_he10")
    val semesterGpa10: String? = null,
    @SerialName("dtb_hk_he4")
    val semesterGpa4: String? = null,
    @SerialName("dtb_tich_luy_he_10")
    val cumulativeGpa10: String? = null,
    @SerialName("dtb_tich_luy_he_4")
    val cumulativeGpa4: String? = null,
    @SerialName("so_tin_chi_dat_hk")
    val creditsPassedSemester: String? = null,
    @SerialName("so_tin_chi_dat_tich_luy")
    val creditsPassedCumulative: String? = null,
    @SerialName("hien_thi_tk_he_10")
    val showGpa10: Boolean,
    @SerialName("hien_thi_tk_he_4")
    val showGpa4: Boolean,
    @SerialName("xep_loai_tkb_hk")
    val semesterRank: String? = null,
    @SerialName("xep_loai_tkb_hk_eg")
    val semesterRankEn: String? = null,
    @SerialName("ds_diem_mon_hoc")
    val subjectGrades: List<SubjectGrade>
)

@Serializable
data class SubjectGrade(
    @SerialName("chuyen_diem_ve_hoc_ky")
    val transferCredit: String,
    @SerialName("ma_mon")
    val subjectCode: String,
    @SerialName("ma_mon_tt")
    val subjectCodeAlt: String? = null,
    @SerialName("nhom_to")
    val groupCode: String,
    @SerialName("ten_mon")
    val subjectName: String,
    @SerialName("ten_mon_eg")
    val subjectNameEn: String? = null,
    @SerialName("mon_hoc_nganh")
    val isMajorSubject: Boolean,
    @SerialName("so_tin_chi")
    val credits: String,
    @SerialName("diem_thi")
    val examGrade: String? = null,
    @SerialName("diem_giua_ky")
    val midtermGrade: String? = null,
    @SerialName("diem_tk")
    val finalGrade: String? = null,
    @SerialName("diem_tk_so")
    val finalGrade4: String? = null,
    @SerialName("diem_tk_chu")
    val finalGradeLetter: String? = null,
    @SerialName("ket_qua")
    val result: Int, // 1 = passed, 0 = failed, -1 = not graded
    @SerialName("hien_thi_ket_qua")
    val showResult: Boolean,
    @SerialName("loai_nganh")
    val majorType: Int,
    @SerialName("KhoaThi")
    val examBlock: Int,
    @SerialName("khong_tinh_diem_tbtl")
    val excludeFromGpa: Int, // 1 = exclude, 0 = include
    @SerialName("ly_do_khong_tinh_diem_tbtl")
    val excludeReason: String,
    @SerialName("ds_diem_thanh_phan")
    val componentGrades: List<ComponentGrade>
)

@Serializable
data class ComponentGrade(
    @SerialName("ky_hieu")
    val symbol: String,
    @SerialName("ten_thanh_phan")
    val componentName: String,
    @SerialName("trong_so")
    val weight: String,
    @SerialName("diem_thanh_phan")
    val grade: String
)
