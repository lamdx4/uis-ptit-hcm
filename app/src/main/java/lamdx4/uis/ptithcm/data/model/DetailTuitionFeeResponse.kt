package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailTuitionFeeResponse(
    @SerialName("data")
    val data: DetailTuitionDetailFeeData,

    @SerialName("result")
    val result: Boolean,

    @SerialName("code")
    val code: Int
)

@Serializable
data class DetailTuitionDetailFeeData(
    @SerialName("total_items")
    val totalItems: Int,

    @SerialName("total_pages")
    val totalPages: Int,

    @SerialName("is_dhmo")
    val isOpenUniversity: Boolean,

    @SerialName("is_tg_dong_hoc_phi")
    val isTuitionPaymentTime: Boolean,

    @SerialName("is_show_don_gia")
    val isShowUnitPrice: Boolean,

    @SerialName("is_show_dot_hoc_nhhk")
    val isShowSemesterBatch: Boolean,

    @SerialName("is_hvsg")
    val isPostgraduate: Boolean,

    @SerialName("no_cu")
    val oldDebt: Double,

    @SerialName("da_mien_giam")
    val totalDiscount: Double,

    @SerialName("ds_phai_thu")
    val payableList: List<PayableItem>,

    @SerialName("ds_da_thu")
    val paidList: List<PaidItem>
)

@Serializable
data class PayableItem(
    @SerialName("hoc_ky")
    val semester: Int,

    @SerialName("ma_nhom_phieu")
    val receiptGroupCode: String,

    @SerialName("ten_nhom_phieu")
    val receiptGroupName: String,

    @SerialName("ma_mon")
    val courseCode: String,

    @SerialName("dien_giai")
    val description: String,

    @SerialName("dien_giai_eg")
    val descriptionEnglish: String? = null,

    @SerialName("so_tin_chi")
    val credits: String,

    @SerialName("so_tin_chi_hp")
    val tuitionCredits: String,

    @SerialName("hoc_phi")
    val tuitionFee: String,

    @SerialName("is_hoc_lai")
    val isRetake: Boolean,

    @SerialName("mien_giam")
    val discount: String,

    @SerialName("phai_thu")
    val amountDue: String,

    @SerialName("don_gia")
    val unitPrice: String
)

@Serializable
data class PaidItem(
    @SerialName("noi_dung_thu")
    val paymentContent: String,

    @SerialName("ma_mon")
    val courseCode: String,

    @SerialName("dien_giai")
    val description: String,

    @SerialName("dien_giai_eg")
    val descriptionEnglish: String? = null,

    @SerialName("da_thu")
    val amountPaid: String,

    @SerialName("ngay_thu")
    val paymentDate: String
)