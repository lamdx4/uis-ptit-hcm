package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import lamdx4.uis.ptithcm.data.model.AcademicResultRequest
import lamdx4.uis.ptithcm.data.model.AcademicResultResponse
import lamdx4.uis.ptithcm.data.model.CompleteStudentInfo
import lamdx4.uis.ptithcm.data.model.SemesterAdditional
import lamdx4.uis.ptithcm.data.model.SemesterFilter
import lamdx4.uis.ptithcm.data.model.SemesterListRequest
import lamdx4.uis.ptithcm.data.model.SemesterListResponse
import lamdx4.uis.ptithcm.data.model.SemesterOrdering
import lamdx4.uis.ptithcm.data.model.SemesterPaging
import lamdx4.uis.ptithcm.data.model.StudentDetailResponse
import lamdx4.uis.ptithcm.data.model.StudentInfoResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentInfoRepository @Inject constructor(private val client: HttpClient) {

    private suspend fun getStudentInfo( maSV: String): StudentInfoResponse {
        return client.post("http://uis.ptithcm.edu.vn/api/sms/w-locthongtinimagesinhvien?MaSV=$maSV") {
            header(HttpHeaders.Accept, "application/json, text/plain, */*")
            header(HttpHeaders.ContentType, ContentType.Text.Plain)
            setBody("")
        }.body()
    }

    private suspend fun getStudentDetail(): StudentDetailResponse {

        val response = client.post("http://uis.ptithcm.edu.vn/api/dkmh/w-locsinhvieninfo") {
            header(HttpHeaders.Accept, "application/json, text/plain, */*")
            header(HttpHeaders.ContentType, ContentType.Text.Plain)
            setBody("")
        }.body<StudentDetailResponse>()

        if (response.result != true || response.data == null) {
            throw Exception("Không thể tải thông tin chi tiết sinh viên")
        }

        return response
    }

    suspend fun getAcademicResult(hocKy: Int): AcademicResultResponse {
        return client.post("http://uis.ptithcm.edu.vn/api/dkmh/w-inketquahoctap") {
            header(HttpHeaders.Accept, "application/json, text/plain, */*")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(AcademicResultRequest(hoc_ky = hocKy))
        }.body()
    }

    suspend fun getAvailableSemesters(): SemesterListResponse {
        val requestBody = SemesterListRequest(
            filter = SemesterFilter(),
            additional = SemesterAdditional(
                paging = SemesterPaging(limit = 100, page = 1),
                ordering = listOf(SemesterOrdering(name = "hoc_ky", order_type = 1))
            )
        )

        return client.post("http://uis.ptithcm.edu.vn/api/dkmh/w-locdshockyketquahoctap") {
            header(HttpHeaders.Accept, "application/json, text/plain, */*")
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(requestBody)
        }.body()
    }

    suspend fun getCompleteStudentInfo( maSV: String): CompleteStudentInfo {

        return coroutineScope {
            val imageInfoDeferred = async { getStudentInfo( maSV) }
            val detailInfoDeferred = async { getStudentDetail() }

            val imageInfo = imageInfoDeferred.await()
            val detailInfo = detailInfoDeferred.await()

            if (!imageInfo.result || detailInfo.result != true || detailInfo.data == null) {
                throw Exception("Không thể tải thông tin sinh viên")
            }

            val imageData = imageInfo.data.thong_tin_sinh_vien
            val detailData = detailInfo.data

            CompleteStudentInfo(
                ma_sv = detailData.ma_sv ?: "",
                ten_day_du = detailData.ten_day_du ?: "",
                gioi_tinh = detailData.gioi_tinh ?: "",
                ngay_sinh = detailData.ngay_sinh ?: "",
                noi_sinh = detailData.noi_sinh ?: "",
                dan_toc = detailData.dan_toc ?: "",
                ton_giao = detailData.ton_giao ?: "",
                quoc_tich = detailData.quoc_tich ?: "",
                dien_thoai = detailData.dien_thoai ?: "",
                email = detailData.email ?: "",
                email2 = detailData.email2 ?: "",
                so_cmnd = detailData.so_cmnd ?: "",
                ho_khau_thuong_tru_gd = detailData.ho_khau_thuong_tru_gd ?: "",
                ho_khau_phuong_xa = detailData.ho_khau_phuong_xa ?: "",
                lop = detailData.lop ?: "",
                khu_vuc = detailData.khu_vuc ?: "",
                doi_tuong_uu_tien = detailData.doi_tuong_uu_tien ?: "",
                khoi = detailData.khoi ?: "",
                nganh = detailData.nganh ?: "",
                nganheg = detailData.nganheg ?: "",
                chuyen_nganh = detailData.chuyen_nganh ?: "",
                khoa = detailData.khoa ?: "",
                bac_he_dao_tao = detailData.bac_he_dao_tao ?: "",
                nien_khoa = detailData.nien_khoa ?: "",
                hien_dien_sv = detailData.hien_dien_sv ?: "",
                ma_cvht = detailData.ma_cvht ?: "",
                ho_ten_cvht = detailData.ho_ten_cvht ?: "",
                email_cvht = detailData.email_cvht ?: "",
                dien_thoai_cvht = detailData.dien_thoai_cvht ?: "",
                ma_truong = detailData.ma_truong ?: "",
                ten_truong = detailData.ten_truong ?: "",
                id_sinh_vien = detailData.id_sinh_vien ?: "",
                id_lop = detailData.id_lop ?: "",
                id_khoa = detailData.id_khoa ?: "",
                id_nganh = detailData.id_nganh ?: "",
                image = imageData.image ?: "",
                thoi_gian_get_data = detailData.thoi_gian_get_data ?: "",
                str_nhhk_vao = detailData.str_nhhk_vao ?: "",
                str_nhhk_ra = detailData.str_nhhk_ra ?: ""
            )
        }
    }

}