package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StudentInfoResponse(
    val data: StudentInfoData,
    val result: Boolean,
    val code: Int
)

@Serializable
data class StudentInfoData(
    val total_items: Int,
    val total_pages: Int,
    val is_dhhn: Boolean,
    val is_luu_thanh_cong: Boolean,
    val is_show_hs_online_sv: Boolean,
    val thong_tin_sinh_vien: StudentInfoDetail,
    val is_trong_thoi_gian_nhap_ly_lich: Boolean,
    val ds_field_bat_buoc: List<String>
)

@Serializable
data class StudentInfoDetail(
    val is_yc_xac_nhan: Boolean,
    val is_save_image: Boolean,
    val is_save_all: Boolean,
    val image: String?,
    val id_sinh_vien: String,
    val id_quoc_tich: String,
    val id_dan_toc: String,
    val id_ton_giao: String,
    val is_doan_vien: Boolean,
    val is_dang_vien: Boolean,
    val is_khac: Boolean,
    val is_dt_dv_da_hoc: Boolean,
    val id_ngan_hang: String,
    val id_thanh_pho_gd: String,
    val id_quan_huyen_gd: String,
    val id_phuong_xa_gd: String,
    val is_mat_cha: Boolean,
    val id_thanh_pho_dcha: String,
    val id_quan_huyen_dcha: String,
    val id_phuong_xa_dcha: String,
    val is_mat_me: Boolean,
    val id_thanh_pho_ll: String,
    val id_quan_huyen_ll: String,
    val id_phuong_xa_ll: String,
    val loai_cu_tru: Int,
    val id_thanh_pho_tt: String,
    val id_quan_huyen_tt: String,
    val id_phuong_xa_tt: String
)