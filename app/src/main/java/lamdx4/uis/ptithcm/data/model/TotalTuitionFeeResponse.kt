package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TotalTuitionFeeResponse(
    @SerialName("data")
    val data: TotalTuitionFeeData,

    @SerialName("result")
    val result: Boolean,

    @SerialName("code")
    val code: Int
)

@Serializable
data class TotalTuitionFeeData(
    @SerialName("total_items")
    val totalItems: Int,

    @SerialName("total_pages")
    val totalPages: Int,

    @SerialName("is_tinh_tong")
    val isCalculateTotal: Boolean,

    @SerialName("is_show_hoc_bong")
    val isShowScholarship: Boolean,

    @SerialName("is_tg_dong_hoc_phi")
    val isTuitionPaymentTime: Boolean,

    @SerialName("is_hvsg")
    val isPostgraduate: Boolean,

    @SerialName("is_show_don_gia")
    val isShowUnitPrice: Boolean,

    @SerialName("ds_hoc_phi_hoc_ky")
    val tuitionFeeList: List<TuitionFeePerSemester>,

    @SerialName("is_dong_hp_theo_edubill")
    val isPayViaEdubill: Boolean,

    @SerialName("url_edubill_gateway")
    val edubillGatewayUrl: String,

    @SerialName("messsage_error_edubill")
    val edubillErrorMessage: String,

    @SerialName("url_call_api")
    val apiCallUrl: String,

    @SerialName("noi_dung_gia_han")
    val extensionContent: String,

    @SerialName("ngay_gia_han")
    val extensionDate: String
)

@Serializable
data class TuitionFeePerSemester(
    @SerialName("nhhk")
    val semesterCode: Int,

    @SerialName("ten_nhom_ct")
    val groupName: String,

    @SerialName("ten_hoc_ky")
    val semesterName: String,

    @SerialName("hoc_phi")
    val tuitionFee: String,

    @SerialName("mien_giam")
    val discount: String,

    @SerialName("duoc_ho_tro")
    val support: String,

    @SerialName("phai_thu")
    val amountDue: String,

    @SerialName("tong_hoc_bong")
    val totalScholarship: String,

    @SerialName("da_thu")
    val amountPaid: String,

    @SerialName("con_no")
    val amountOwed: String,

    @SerialName("ghi_chu")
    val note: String,

    @SerialName("don_gia")
    val unitPrice: String
)
