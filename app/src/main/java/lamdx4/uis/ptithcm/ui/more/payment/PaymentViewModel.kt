package lamdx4.uis.ptithcm.ui.more.payment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.repository.PaymentFormData
import lamdx4.uis.ptithcm.data.repository.PaymentRepository
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(private val repo: PaymentRepository) :
    ViewModel() {
    companion object {
        val TAG = "PaymentViewModel"
    }

    private val _formState: MutableStateFlow<PaymentFormData?> = MutableStateFlow(null)
    val formState = _formState.asStateFlow()

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

    fun check(studentId: String, captcha: String) {
        viewModelScope.launch {
            try {
                if (formState.value == null) return@launch
                val r = repo.check(studentId, captcha, formState.value!!)
                if (r.isSuccess) {
                    _formState.value = r.getOrThrow().newFormData
                }
            } catch (e: Exception) {
                Log.e(TAG, "Lỗi khi check thông tin thanh toán", e)
            }
        }
    }
}
