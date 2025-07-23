package lamdx4.uis.ptithcm.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response from API w-locdsdiemsinhvien
 */
data class GradeResponse(
    @SerializedName("data")
    val data: GradeData,
    @SerializedName("result")
    val result: Boolean,
    @SerializedName("code")
    val code: Int
)

data class GradeData(
    @SerializedName("total_items")
    val totalItems: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("is_kkbd")
    val isKkbd: Boolean,
    @SerializedName("ds_diem_hocky")
    val semesterGrades: List<SemesterGrade>,
    @SerializedName("ds_field_an_cot_diem")
    val hiddenGradeFields: List<String>,
    @SerializedName("hien_thi_khoa_thi")
    val showExamBlock: Boolean,
    @SerializedName("hien_thi_cot_diem_tp")
    val showComponentGrades: Boolean,
    @SerializedName("an_chi_tiet_diem_tp")
    val hideComponentDetails: Boolean,
    @SerializedName("hien_thi_cot_diem_k1")
    val showMidtermGrades: Boolean,
    @SerializedName("hien_thi_cot_mhtt")
    val showSubjectCode: Boolean,
    @SerializedName("hien_thi_cot_stctt")
    val showCreditCode: Boolean,
    @SerializedName("hien_thi_cot_diemtk10")
    val showGrade10: Boolean,
    @SerializedName("hien_thi_cot_diemtk4")
    val showGrade4: Boolean,
    @SerializedName("hien_thi_cot_diem_thi")
    val showExamGrade: Boolean,
    @SerializedName("hien_thi_cot_mh_nganh")
    val showMajorSubject: Boolean,
    @SerializedName("hien_thi_cot_hk_chuyen_diem")
    val showTransferCredit: Boolean,
    @SerializedName("mesage_diemtk4")
    val messageGrade4: String,
    @SerializedName("mesage_diemtkc")
    val messageGradeC: String,
    @SerializedName("mesage_diemtk10")
    val messageGrade10: String,
    @SerializedName("mesage_diemk1")
    val messageMidterm: String
)

data class SemesterGrade(
    @SerializedName("loai_nganh")
    val majorType: Int? = null,
    @SerializedName("hoc_ky")
    val semesterCode: String,
    @SerializedName("ten_hoc_ky")
    val semesterName: String,
    @SerializedName("dtb_hk_he10")
    val semesterGpa10: String? = null,
    @SerializedName("dtb_hk_he4")
    val semesterGpa4: String? = null,
    @SerializedName("dtb_tich_luy_he_10")
    val cumulativeGpa10: String? = null,
    @SerializedName("dtb_tich_luy_he_4")
    val cumulativeGpa4: String? = null,
    @SerializedName("so_tin_chi_dat_hk")
    val creditsPassedSemester: String? = null,
    @SerializedName("so_tin_chi_dat_tich_luy")
    val creditsPassedCumulative: String? = null,
    @SerializedName("hien_thi_tk_he_10")
    val showGpa10: Boolean,
    @SerializedName("hien_thi_tk_he_4")
    val showGpa4: Boolean,
    @SerializedName("xep_loai_tkb_hk")
    val semesterRank: String? = null,
    @SerializedName("xep_loai_tkb_hk_eg")
    val semesterRankEn: String? = null,
    @SerializedName("ds_diem_mon_hoc")
    val subjectGrades: List<SubjectGrade>
)

data class SubjectGrade(
    @SerializedName("chuyen_diem_ve_hoc_ky")
    val transferCredit: String,
    @SerializedName("ma_mon")
    val subjectCode: String,
    @SerializedName("ma_mon_tt")
    val subjectCodeAlt: String? = null,
    @SerializedName("nhom_to")
    val groupCode: String,
    @SerializedName("ten_mon")
    val subjectName: String,
    @SerializedName("ten_mon_eg")
    val subjectNameEn: String? = null,
    @SerializedName("mon_hoc_nganh")
    val isMajorSubject: Boolean,
    @SerializedName("so_tin_chi")
    val credits: String,
    @SerializedName("diem_thi")
    val examGrade: String? = null,
    @SerializedName("diem_giua_ky")
    val midtermGrade: String? = null,
    @SerializedName("diem_tk")
    val finalGrade: String? = null,
    @SerializedName("diem_tk_so")
    val finalGrade4: String? = null,
    @SerializedName("diem_tk_chu")
    val finalGradeLetter: String? = null,
    @SerializedName("ket_qua")
    val result: Int, // 1 = passed, 0 = failed, -1 = not graded
    @SerializedName("hien_thi_ket_qua")
    val showResult: Boolean,
    @SerializedName("loai_nganh")
    val majorType: Int,
    @SerializedName("KhoaThi")
    val examBlock: Int,
    @SerializedName("khong_tinh_diem_tbtl")
    val excludeFromGpa: Int, // 1 = exclude, 0 = include
    @SerializedName("ly_do_khong_tinh_diem_tbtl")
    val excludeReason: String,
    @SerializedName("ds_diem_thanh_phan")
    val componentGrades: List<ComponentGrade>
)

data class ComponentGrade(
    @SerializedName("ky_hieu")
    val symbol: String,
    @SerializedName("ten_thanh_phan")
    val componentName: String,
    @SerializedName("trong_so")
    val weight: String,
    @SerializedName("diem_thanh_phan")
    val grade: String
)
