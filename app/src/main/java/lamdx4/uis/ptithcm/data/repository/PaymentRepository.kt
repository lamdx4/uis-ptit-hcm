    package lamdx4.uis.ptithcm.data.repository

    import android.util.Log
    import io.ktor.client.HttpClient
    import io.ktor.client.call.body
    import io.ktor.client.plugins.HttpRedirect
    import io.ktor.client.request.forms.FormDataContent
    import io.ktor.client.request.get
    import io.ktor.client.request.post
    import io.ktor.client.request.setBody
    import io.ktor.client.statement.request
    import io.ktor.http.ContentType
    import io.ktor.http.Parameters
    import io.ktor.http.contentType
    import io.ktor.http.headers
    import lamdx4.uis.ptithcm.di.RefreshClient
    import lamdx4.uis.ptithcm.util.invalidateBearerTokens
    import org.jsoup.Jsoup
    import org.jsoup.nodes.Document
    import java.net.URL
    import javax.inject.Inject
    import javax.inject.Singleton


    @Singleton
    class PaymentRepository @Inject constructor(
        @param:RefreshClient private val client: HttpClient
    ) : Cacheable {
        private companion object {
            const val TAG: String = "PaymentRepository"
        }

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
                val res = client.post("https://pay.ptithcm.edu.vn/check/") {
                    ->
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
                }

                val html = res.body<String>()
                val doc = Jsoup.parse(html)
                val dataForm = parsePaymentForm(doc)

                val data = if (res.request.url.toString().contains("pay.ptithcm.edu.vn/check/")) {
                    CheckPaymentData(
                        isSuccess = false,
                        newFormData = dataForm
                    )
                } else {
                    CheckPaymentData(
                        isSuccess = true,
                        studentPaymentInfo = parseStudentPaymentInfo(doc),
                        newFormData = dataForm
                    )
                }

                Result.success(data)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    //    suspend fun refreshCaptcha(data: PaymentFormData): Result<PaymentFormData> {

        suspend fun pay(
            captcha: String,
            btnPay: String,
            data: PaymentFormData
        ): Result<PayCheckStatus> {
            return try {
                val clientNoRedirect = client.config {

    //                followRedirects = false
                }

                Log.d(TAG, "Request parameters:")
                val params = Parameters.build {
                    append("__EVENTTARGET", data.eventTarget)
                    append("__EVENTARGUMENT", data.eventArgument)
                    append("__VIEWSTATE", data.viewState)
                    append("__VIEWSTATEGENERATOR", data.viewStateGenerator)
                    append("__EVENTVALIDATION", data.eventValidation)
                    append("txtMaXNhan", captcha)
                    append("cboLanguage", "vn")
                    append("txtExpire", data.timeExpire.toString())
                    append("btnPay", btnPay)
                }
                params.forEach { key, values ->
                    Log.d(TAG, "$key: ${values.firstOrNull()}")
                }

                val redirectUrls = mutableListOf<String>()
                var currentResponse = clientNoRedirect.post("https://pay.ptithcm.edu.vn/") {
                    contentType(ContentType.Application.FormUrlEncoded)
                    headers {
                        clear()
                        append(
                            "Accept",
                            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
                        )
                        append("Accept-Language", "vi,en;q=0.9")
                        append("Cache-Control", "max-age=0")
                        append("Referer", "https://pay.ptithcm.edu.vn/")
                        append("Upgrade-Insecure-Requests", "1")
                    }
                    setBody(
                        FormDataContent(
                            Parameters.build {
                                append("__EVENTTARGET", data.eventTarget)
                                append("__EVENTARGUMENT", data.eventArgument)
                                append("__VIEWSTATE", data.viewState)
                                append("__VIEWSTATEGENERATOR", data.viewStateGenerator)
                                append("__EVENTVALIDATION", data.eventValidation)
                                append("txtMaXNhan", captcha)
                                append("cboLanguage", "vn")
                                append("txtExpire", data.timeExpire.toString())
                                append("btnPay", btnPay)
                            }
                        )
                    )
                }

                // Follow redirects manually và collect URLs
                while (currentResponse.status.value in 300..399) {
                    val location = currentResponse.headers["Location"]
                    if (location != null) {
                        redirectUrls.add(location)

                        // Check nếu đã đến VNPay thì dừng
                        if (location.contains("pay.vnpay.vn")) {
                            break
                        }

                        // Continue redirect
                        currentResponse = clientNoRedirect.get(location)
                    } else {
                        break
                    }
                }

                val html = currentResponse.body<String>()
                val doc = Jsoup.parse(html)

                val payCheckStatus = if (redirectUrls.any { it.contains("pay.vnpay.vn") }) {
                    PayCheckStatus.Success(
                        redirectUrls.find { it.contains("pay.vnpay.vn") }!!
                    )
                } else {
                    PayCheckStatus.Failure(
                        newFormData = parsePaymentForm(doc),
                    )
                }

                Log.d(TAG, redirectUrls.toString())

                Result.success(payCheckStatus)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        private fun parsePaymentForm(document: Document): PaymentFormData {
            val baseUrl = "https://pay.ptithcm.edu.vn/check/"
            val relativeSrc = document.selectFirst("img[src*=CaptchaImage.axd]")?.attr("src")
            val fullUrl = relativeSrc?.let { URL(URL(baseUrl), it).toString() }
            val timeExpire = try {
                document.select("input[name=txtExpire]").attr("value").toLong()
            } catch (e: Exception) {
                -1
            }
            return PaymentFormData(
                eventTarget = document.select("input[name=__EVENTTARGET]").attr("value"),
                eventArgument = document.select("input[name=__EVENTARGUMENT]").attr("value"),
                viewState = document.select("input[name=__VIEWSTATE]").attr("value"),
                viewStateGenerator = document.select("input[name=__VIEWSTATEGENERATOR]").attr("value"),
                eventValidation = document.select("input[name=__EVENTVALIDATION]").attr("value"),
                imgCaptchaUrl = fullUrl ?: "",
                message = document.selectFirst("#lblMessage")?.text()?.takeIf { it.isNotBlank() } ?: "",
                timeExpire = timeExpire // System.currentTimeMillis() + 1000 * 60 * 10
            )
        }

        fun parseStudentPaymentInfo(document: Document): StudentPaymentInfo {
            val fullName = document.getElementById("lblHoTenSV")?.text()?.trim()
                ?: error("Missing #lblHoTenSV")
            val studentId = document.getElementById("lblMaSV")?.text()?.trim()
                ?: error("Missing #lblMaSV")
            val classCode = document.getElementById("lblLop")?.text()?.trim()
                ?: error("Missing #lblLop")
            val amountText = document.getElementById("lblSoTien")?.text()?.trim()
                ?: error("Missing #lblSoTien")
            val description = document.getElementById("lblNoiDungTT")?.text()?.trim()
                ?: error("Missing #lblNoiDungTT")

            val amountVnd = amountText

            return StudentPaymentInfo(
                fullName = fullName,
                studentId = studentId,
                classCode = classCode,
                amountVnd = amountVnd,
                description = description
            )
        }

        override fun clearCache() {
            this.client.invalidateBearerTokens()
            // Hiện tại repo này chưa có cache
        }

    }

    data class PaymentFormData(
        val eventTarget: String,
        val eventArgument: String,
        val viewState: String,
        val viewStateGenerator: String,
        val eventValidation: String,
        val imgCaptchaUrl: String,
        val message: String = "",
        val timeExpire: Long = -1
    )

    data class CheckPaymentData(
        val isSuccess: Boolean,
        val studentPaymentInfo: StudentPaymentInfo? = null,
        val newFormData: PaymentFormData? = null
    )

    data class StudentPaymentInfo(
        val fullName: String,
        val studentId: String,
        val classCode: String,
        val amountVnd: String,
        val description: String
    )

    sealed interface PayCheckStatus {
        data class Success(val url: String) : PayCheckStatus
        data class Failure(val newFormData: PaymentFormData? = null) : PayCheckStatus
    }