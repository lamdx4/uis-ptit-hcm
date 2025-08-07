package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvoiceRequest(
    val filter: InvoiceFilter,
    val additional: InvoiceAdditional
)

@Serializable
data class InvoiceFilter(
    @SerialName("hoc_ky") val semester: Int,
    @SerialName("tim_kiem") val keyword: String
)

@Serializable
data class InvoiceAdditional(
    val paging: InvoicePaging,
    val ordering: List<InvoiceOrdering>
)

@Serializable
data class InvoicePaging(
    val limit: Int,
    val page: Int
)

@Serializable
data class InvoiceOrdering(
    val name: String,
    @SerialName("order_type") val orderType: Int
)
