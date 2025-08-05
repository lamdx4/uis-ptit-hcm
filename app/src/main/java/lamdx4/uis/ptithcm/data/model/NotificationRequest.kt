package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationRequest(
    val filter: Filter,
    val additional: Additional
)

@Serializable
data class Filter(
    val id: String? = null,
    @SerialName("is_noi_dung") val hasContent: Boolean,
    @SerialName("is_web") val isWeb: Boolean
)

@Serializable
data class Additional(
    val paging: Paging,
    val ordering: List<Ordering>
)

@Serializable
data class Paging(
    val limit: Int,
    val page: Int
)

@Serializable
data class Ordering(
    val name: String,
    @SerialName("order_type") val orderType: Int
)
