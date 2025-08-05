package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val data: NotificationData,
    val result: Boolean,
    val code: Int
)

@Serializable
data class NotificationData(
    @SerialName("total_items") val totalItems: Int,
    @SerialName("total_pages") val totalPages: Int,
    val notification: Int,
    @SerialName("ds_thong_bao") val notificationList: List<NotificationItem>
)

@Serializable
data class NotificationItem(
    val id: String,
    @SerialName("doi_tuong_search") val targetSearch: String,
    @SerialName("doi_tuong") val targetType: Int,
    @SerialName("phan_cap_search") val levelSearch: String,
    @SerialName("phan_cap_sinh_vien") val studentLevel: Int,
    @SerialName("tieu_de") val title: String,
    @SerialName("noi_dung") val content: String,
    @SerialName("is_phai_xem") val mustView: Boolean,
    @SerialName("ngay_gui") val sentAt: String,
    @SerialName("nguoi_gui") val sender: String,
    @SerialName("is_da_doc") val isRead: Boolean,
    @SerialName("ds_doi_tuong") val targetList: List<String>,
    @SerialName("is_xem_phan_hoi") val hasFeedbackView: Boolean
)
