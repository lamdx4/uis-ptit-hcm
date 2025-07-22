import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import lamdx4.uis.ptithcm.data.model.StudentInfoResponse
import io.ktor.client.call.body

class StudentRepository {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) { json() }
    }

    suspend fun getStudentInfo(accessToken: String, maSV: String): StudentInfoResponse {
        return client.post("http://uis.ptithcm.edu.vn/api/sms/w-locthongtinimagesinhvien?MaSV=$maSV") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            header(HttpHeaders.Accept, "application/json, text/plain, */*")
            header(HttpHeaders.ContentType, ContentType.Text.Plain)
            setBody("")
        }.body()
    }
}