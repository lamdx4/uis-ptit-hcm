package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AcademicResultRequest(
    val hoc_ky: Int
)

@Serializable
data class AcademicResultResponse(
    val data: AcademicResultData? = null,
    val result: Boolean,
    val code: Int
)

@Serializable
data class AcademicResultData(
    // Thông tin tổng quan từ API
    val so_luong1: Int? = null,
    val so_luong2: Int? = null,
    val so_luong3: Int? = null,
    val so_luong4: Int? = null,
    val diem_trung_binh1: Double? = null,
    val diem_trung_binh2: Double? = null,
    val diem_trung_binh3: Double? = null,
    val ghi_chu_1: String? = null,
    
    // Danh sách kết quả từng môn
    val ds_du_lieu: List<SubjectResult>? = null,
    
    // Danh sách thời khóa biểu trong ngày
    val ds_tkb_trong_ngay: List<String>? = null,
    
    // Số lượng thông báo chưa đọc
    val sl_thong_bao_chua_doc: Int? = null
) {
    // Computed properties để tương thích với UI
    val tong_so_tin_chi: Int? get() = ds_du_lieu?.sumOf { it.so_tin_chi ?: 0 }
    val tong_so_mon: Int? get() = ds_du_lieu?.size
    val diem_trung_binh: Double? get() = diem_trung_binh2
    val thong_ke_diem: GradeStatistics? get() = createGradeStatistics()
    
    private fun createGradeStatistics(): GradeStatistics? {
        val subjects = ds_du_lieu ?: return null
        var countA = 0
        var countB = 0  
        var countC = 0
        var countD = 0
        var countF = 0
        
        subjects.forEach { subject ->
            val grade = subject.diem_trung_binh2 ?: 0.0
            when {
                grade >= 8.5 -> countA++
                grade >= 7.0 -> countB++
                grade >= 5.5 -> countC++
                grade >= 4.0 -> countD++
                else -> countF++
            }
        }
        
        val total = subjects.size
        return GradeStatistics(
            so_mon_A = countA,
            so_mon_B = countB,
            so_mon_C = countC,
            so_mon_D = countD,
            so_mon_F = countF,
            ty_le_A = if (total > 0) countA * 100.0 / total else 0.0,
            ty_le_B = if (total > 0) countB * 100.0 / total else 0.0,
            ty_le_C = if (total > 0) countC * 100.0 / total else 0.0,
            ty_le_D = if (total > 0) countD * 100.0 / total else 0.0,
            ty_le_F = if (total > 0) countF * 100.0 / total else 0.0
        )
    }
}

@Serializable
data class SubjectResult(
    val ma_doi_tuong: String,
    val ten_doi_tuong: String,
    val ten_doi_tuong_eg: String? = null,
    val so_luong1: Int? = null,
    val so_luong2: Int? = null,
    val so_luong3: Int? = null,
    val diem_trung_binh1: Double? = null,
    val diem_trung_binh2: Double? = null
) {
    // Computed properties để tương thích với UI cũ
    val ma_mon: String get() = ma_doi_tuong
    val ten_mon: String get() = ten_doi_tuong
    val so_tin_chi: Int? get() = diem_trung_binh1?.toInt()
    val diem_so: Double? get() = diem_trung_binh2
    val diem_chu: String? get() = when {
        diem_trung_binh2 == null || diem_trung_binh2 == 0.0 -> null
        diem_trung_binh2!! >= 8.5 -> "A"
        diem_trung_binh2!! >= 7.0 -> "B"
        diem_trung_binh2!! >= 5.5 -> "C"
        diem_trung_binh2!! >= 4.0 -> "D"
        else -> "F"
    }
    val ket_qua: String? get() = if (diem_trung_binh2 != null && diem_trung_binh2!! >= 4.0) "Đạt" else "Không đạt"
}

@Serializable
data class GradeStatistics(
    val so_mon_A: Int = 0,
    val so_mon_B: Int = 0,
    val so_mon_C: Int = 0,
    val so_mon_D: Int = 0,
    val so_mon_F: Int = 0,
    val ty_le_A: Double = 0.0,
    val ty_le_B: Double = 0.0,
    val ty_le_C: Double = 0.0,
    val ty_le_D: Double = 0.0,
    val ty_le_F: Double = 0.0
)
