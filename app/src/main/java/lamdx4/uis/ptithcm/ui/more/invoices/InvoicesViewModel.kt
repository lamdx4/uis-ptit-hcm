package lamdx4.uis.ptithcm.ui.more.invoices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.InvoiceResponse
import lamdx4.uis.ptithcm.data.repository.InvoicesRepository
import javax.inject.Inject

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val invoicesRepository: InvoicesRepository
) : ViewModel() {
    private val _invoicesState = MutableStateFlow<InvoiceResponse?>(null)
    val invoicesState = _invoicesState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    fun loadInvoices() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = invoicesRepository.getInvoices()
            result.onSuccess { invoices ->
                _invoicesState.value = invoices
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải danh sách hóa đơn"
            }
            _isLoading.value = false
        }
    }

    fun refreshInvoices() {
        loadInvoices()
    }
}
