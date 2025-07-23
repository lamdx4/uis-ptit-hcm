package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CompleteStudentInfo(
    // Thông tin cơ bản từ API detail
    val ma_sv: String,
    val ten_day_du: String,
    val gioi_tinh: String,
    val ngay_sinh: String,
    val noi_sinh: String,
    val dan_toc: String,
    val ton_giao: String,
    val quoc_tich: String,
    val dien_thoai: String,
    val email: String,
    val email2: String,
    val so_cmnd: String,
    val ho_khau_thuong_tru_gd: String,
    val ho_khau_phuong_xa: String,
    
    // Thông tin học tập
    val lop: String,
    val khu_vuc: String,
    val doi_tuong_uu_tien: String,
    val khoi: String,
    val nganh: String,
    val nganheg: String,
    val chuyen_nganh: String,
    val khoa: String,
    val bac_he_dao_tao: String,
    val nien_khoa: String,
    val hien_dien_sv: String,
    
    // Thông tin cố vấn học tập
    val ma_cvht: String,
    val ho_ten_cvht: String,
    val email_cvht: String,
    val dien_thoai_cvht: String,
    
    // Thông tin trường
    val ma_truong: String,
    val ten_truong: String,
    
    // Thông tin IDs
    val id_sinh_vien: String,
    val id_lop: String,
    val id_khoa: String,
    val id_nganh: String,
    
    // Ảnh từ API image
    val image: String?,
    
    // Thời gian
    val thoi_gian_get_data: String,
    val str_nhhk_vao: String,
    val str_nhhk_ra: String
)
