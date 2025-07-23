package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SemesterListRequest(
    val filter: SemesterFilter,
    val additional: SemesterAdditional
)

@Serializable
data class SemesterFilter(
    val is_tieng_anh: String? = null
)

@Serializable
data class SemesterAdditional(
    val paging: SemesterPaging,
    val ordering: List<SemesterOrdering>
)

@Serializable
data class SemesterPaging(
    val limit: Int,
    val page: Int
)

@Serializable
data class SemesterOrdering(
    val name: String,
    val order_type: Int // 1 = ascending, -1 = descending
)

@Serializable
data class SemesterListResponse(
    val data: SemesterListData? = null,
    val result: Boolean,
    val code: Int
)

@Serializable
data class SemesterListData(
    val ds_hoc_ky: List<SemesterInfo>? = null,
    val total_items: Int = 0,
    val total_pages: Int = 0
)

@Serializable
data class SemesterInfo(
    val hoc_ky: Int,
    val ten_hoc_ky: String,
    val nam_hoc: String? = null,
    val bat_dau: String? = null,
    val ket_thuc: String? = null,
    val is_hien_tai: Boolean = false,
    val so_mon: Int = 0,
    val diem_trung_binh: Double? = null
)
