package lamdx4.uis.ptithcm.ui.more.invoices

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.Invoice
import lamdx4.uis.ptithcm.data.model.InvoiceResponse
import lamdx4.uis.ptithcm.data.repository.InvoicesRepository
import javax.inject.Inject

private const val PAGINATION_LIMIT = 10

@HiltViewModel
class InvoicesViewModel @Inject constructor(
    private val invoicesRepository: InvoicesRepository
) : ViewModel() {
    // Lưu danh sách tất cả invoice đã tải
    private val _allInvoices = mutableListOf<Invoice>()
    private var currentPage = 1
    private var totalPages = 1

    private val _invoicesState = MutableStateFlow<InvoiceResponse?>(null)
    val invoicesState = _invoicesState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing

    init {
        loadInvoices(page = 1)
    }

    fun loadInvoices(page: Int, isForceRefresh: Boolean = false) {
        if (page > totalPages) return // đã hết trang
        if (_isRefreshing.value) return // đang tải
        if (isForceRefresh) _isRefreshing.value = true
        viewModelScope.launch {
            _isLoading.value = true
            val result = invoicesRepository.getInvoices(
                limit = PAGINATION_LIMIT,
                page = page,
                isForceRefresh = isForceRefresh
            )
            result.onSuccess { response ->
                // cập nhật totalPages từ API
                totalPages = response.data.totalPages

                if (page == 1) {
                    _allInvoices.clear()
                }

                // Thêm dữ liệu mới
                _allInvoices.addAll(response.data.invoices)
                // Cập nhật flow cho UI
                _invoicesState.value = response.copy(
                    data = response.data.copy(
                        invoices = _allInvoices.toList()
                    )
                )
                _errorMessage.value = null
                currentPage = page
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải danh sách hóa đơn"
            }
            _isLoading.value = false
            if (isForceRefresh) _isRefreshing.value = false
        }
    }

    fun loadNextPage() {
        loadInvoices(currentPage + 1)
    }

    fun refreshInvoices() {
        loadInvoices(page = 1, true)
    }
}
