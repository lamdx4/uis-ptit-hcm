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
    val thong_tin_sinh_vien: StudentInfoDetail
    // ... các trường khác nếu cần
)

@Serializable
data class StudentInfoDetail(
    val image: String?, // Chuỗi base64
    val id_sinh_vien: String,
    // ... các trường khác nếu cần
)