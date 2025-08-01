package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(
    private val client: HttpClient
) {
    suspend fun fetchPaymentForm(): Result<PaymentFormData> {
        return try {
            val html = client.get("https://pay.ptithcm.edu.vn/check/").body<String>()

            Result.success(
                parsePaymentForm(
                    Jsoup.parse(html)
                )
            )

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun check(
        studentId: String, captcha: String,
        data: PaymentFormData
    ): Result<CheckPaymentData> {
        return try {
            val html = client.post("https://pay.ptithcm.edu.vn/check/") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(
                        (Parameters.build {
                            append("__EVENTTARGET", data.eventTarget)
                            append("__EVENTARGUMENT", data.eventArgument)
                            append("__VIEWSTATE", data.viewState)
                            append("__VIEWSTATEGENERATOR", data.viewStateGenerator)
                            append("__EVENTVALIDATION", data.eventValidation)
                            append("txt_billing_masv", studentId)
                            append("txt_billing_xn", captcha)
                            append("btnPay", "Thanh toán")
                        })
                    )
                )
            }.body<String>()

            val doc = Jsoup.parse(html)
            val dataForm = parsePaymentForm(doc)

            val result = if (dataForm.message.isNotEmpty()) {
                CheckPaymentData(
                    isSuccess = false,
                    newFormData = dataForm
                )
            } else {
                CheckPaymentData(
                    isSuccess = true,
                    newFormData = dataForm
                )
            }

            Result.success(result)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private fun parsePaymentForm(document: Document): PaymentFormData {
        val baseUrl = "https://pay.ptithcm.edu.vn/check/"
        val relativeSrc = document.selectFirst("img[src*=CaptchaImage.axd]")?.attr("src")
        val fullUrl = relativeSrc?.let { URL(URL(baseUrl), it).toString() }

        return PaymentFormData(
            eventTarget = document.select("input[name=__EVENTTARGET]").attr("value"),
            eventArgument = document.select("input[name=__EVENTARGUMENT]").attr("value"),
            viewState = document.select("input[name=__VIEWSTATE]").attr("value"),
            viewStateGenerator = document.select("input[name=__VIEWSTATEGENERATOR]").attr("value"),
            eventValidation = document.select("input[name=__EVENTVALIDATION]").attr("value"),
            imgCaptchaUrl = fullUrl ?: "",
            message = document.selectFirst("#lblMessage")?.text()?.takeIf { it.isNotBlank() } ?: ""
        )
    }

}

data class PaymentFormData(
    val eventTarget: String,
    val eventArgument: String,
    val viewState: String,
    val viewStateGenerator: String,
    val eventValidation: String,
    val imgCaptchaUrl: String,
    val message: String = ""
)

data class CheckPaymentData(
    val isSuccess: Boolean,
    val newFormData: PaymentFormData? = null
)