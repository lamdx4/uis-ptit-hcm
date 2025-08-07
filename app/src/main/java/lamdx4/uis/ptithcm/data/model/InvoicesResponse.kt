package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvoiceResponse(
    val data: InvoiceData,
    val result: Boolean,
    val code: Int
)

@Serializable
data class InvoiceData(
    @SerialName("total_items") val totalItems: Int,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("is_viettel") val isViettel: Boolean,
    @SerialName("is_vnpt") val isVnpt: Boolean,
    @SerialName("is_misa") val isMisa: Boolean,
    @SerialName("is_wintech") val isWintech: Boolean,
    @SerialName("ds_hoa_don_dien_tu") val invoices: List<Invoice>
)

@Serializable
data class Invoice(
    @SerialName("ma_sinh_vien") val studentCode: String,
    @SerialName("ten_day_du") val fullName: String,
    @SerialName("ngay_sinh") val dateOfBirth: String,
    @SerialName("ma_lop") val classCode: String,
    @SerialName("id_phieu_thu") val receiptId: String,
    @SerialName("so_hoa_don") val invoiceNumber: String,
    @SerialName("tien_phieu_thu") val amount: Double,
    @SerialName("ngay_thu_chi") val paymentDate: String,
    @SerialName("ngay_lap_hoa_don") val invoiceDate: String,
    @SerialName("nguoi_lap_hoa_don") val createdBy: String,
    @SerialName("hoc_ky") val semester: Int,
    @SerialName("code_hoa_don") val invoiceCode: String? = null,
    @SerialName("ghi_chu") val note: String
)
