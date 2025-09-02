package lamdx4.uis.ptithcm.ui.more.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.repository.PayCheckStatus
import lamdx4.uis.ptithcm.data.repository.PaymentFormData
import lamdx4.uis.ptithcm.data.repository.PaymentRepository
import lamdx4.uis.ptithcm.data.repository.StudentPaymentInfo
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(private val repo: PaymentRepository) :
    ViewModel() {
    companion object {
        val TAG = "PaymentViewModel"
    }

    private val _formState: MutableStateFlow<PaymentFormData?> = MutableStateFlow(null)
    val formState = _formState.asStateFlow()

    private val _studentPaymentInfo: MutableStateFlow<StudentPaymentInfo?> = MutableStateFlow(null)
    val studentPaymentInfo = _studentPaymentInfo.asStateFlow()

    private val currentPageState: MutableStateFlow<String> = MutableStateFlow("Check")
    val currentPage = currentPageState.asStateFlow()

    private val _payUrl: MutableStateFlow<String?> = MutableStateFlow(null)
    val payUrl = _payUrl.asStateFlow()

    fun fetchForm() {
        viewModelScope.launch {
            try {
                val r = repo.fetchPaymentForm()
                if (r.isSuccess) {
                    _formState.value = r.getOrThrow()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi fetch form", e)
            }
        }
    }

    fun check(
        studentId: String,
        captcha: String
    ) {
        viewModelScope.launch {
            try {
                if (formState.value == null) return@launch
                val r = repo.check(studentId, captcha, formState.value!!)
                if (r.isSuccess) {
                    _formState.value = r.getOrThrow().newFormData
                }
                if (r.getOrThrow().isSuccess) {
                    currentPageState.value = "Payment-Check"
                    _formState.value = r.getOrThrow().newFormData
                    _studentPaymentInfo.value = r.getOrThrow().studentPaymentInfo
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi check thông tin thanh toán", e)
            }
        }
    }

    fun pay(
        captcha: String,
        btnPay: String
    ) {
        viewModelScope.launch {
            try {
                if (formState.value == null) return@launch
                val r = repo.pay(captcha, btnPay, formState.value!!)
                if (r.isSuccess) {
                    if (r.getOrThrow() is PayCheckStatus.Success) {
                        _payUrl.value = (r.getOrThrow() as PayCheckStatus.Success).url
                        currentPageState.value = "Payment"
                    } else if (r.getOrThrow() is PayCheckStatus.Failure) {
                        _formState.value = (r.getOrThrow() as PayCheckStatus.Failure).newFormData
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi check thông tin thanh toán", e)
            }
        }
    }
}
