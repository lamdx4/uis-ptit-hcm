package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response from course registration/unregistration API
 */
@Serializable
data class RegisterActionResponse(
    @SerialName("data")
    val data: RegisterActionData,
    
    @SerialName("result")
    val result: Boolean,
    
    @SerialName("code")
    val code: Int,
    
    @SerialName("message")
    val message: String = ""
)

@Serializable
data class RegisterActionData(
    @SerialName("is_thanh_cong")
    val isSuccess: Boolean,
    
    @SerialName("thong_bao_loi")
    val errorMessage: String,
    
    @SerialName("is_chung_nhom_mon_hoc")
    val isConflictingGroup: Boolean = false,
    
    @SerialName("is_show_nganh_hoc")
    val showMajor: Boolean = false,
    
    @SerialName("ket_qua_dang_ky")
    val registrationResult: RegistrationResult? = null,
    
    @SerialName("thong_bao_tien_quyet")
    val prerequisiteWarning: String = "",
    
    @SerialName("id_to_hoc_xoa")
    val deletedGroupId: String = "",
    
    @SerialName("is_choose")
    val isChoose: Boolean = false,
    
    @SerialName("id_to_hoc_choose")
    val chosenGroupId: String = "0"
)

@Serializable
data class RegistrationResult(
    @SerialName("id_kqdk")
    val registrationId: String,
    
    @SerialName("trang_thai_mon")
    val subjectStatus: String = "",
    
    @SerialName("ngay_dang_ky")
    val registeredAt: String,
    
    @SerialName("nguoi_dang_ky")
    val registeredBy: String = "",
    
    @SerialName("is_da_rut_mon_hoc")
    val isWithdrawn: Boolean = false,
    
    @SerialName("enable_xoa")
    val canDelete: Boolean,
    
    @SerialName("dien_giai_enable_xoa")
    val deleteReason: String = "",
    
    @SerialName("hoc_phi_tam_tinh")
    val estimatedFee: Double,
    
    @SerialName("to_hoc")
    val subjectGroup: SubjectGroup? = null,
    
    @SerialName("id_dia_diem_thi")
    val examLocationId: String = "0",
    
    @SerialName("ten_dia_diem_thi")
    val examLocationName: String = ""
)
