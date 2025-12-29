package lamdx4.uis.ptithcm.ui.fee

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.DetailTuitionFeeResponse
import lamdx4.uis.ptithcm.data.model.TotalTuitionFeeResponse
import lamdx4.uis.ptithcm.data.model.TuitionFeeSemestersResponse
import lamdx4.uis.ptithcm.data.repository.FeeRepository
import javax.inject.Inject

@HiltViewModel
class FeeViewModel @Inject constructor(
    private val feeRepository: FeeRepository
) : ViewModel() {
    private val _totalTuitionFee = MutableStateFlow<TotalTuitionFeeResponse?>(null)
    val totalTuitionFee = _totalTuitionFee

    private val _tuitionFeeSemester = MutableStateFlow<TuitionFeeSemestersResponse?>(null)
    val tuitionFeeSemester = _tuitionFeeSemester

    private val _detailTuitionFee = MutableStateFlow<DetailTuitionFeeResponse?>(null)
    val detailTuitionFee = _detailTuitionFee

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    init {
        viewModelScope.launch {
            loadTotalTuitionFee()

            loadTuitionFeeSemester()
            val semester = tuitionFeeSemester
                .filterNotNull()
                .map { it.data.semesterList.firstOrNull()?.semesterCode }
                .filterNotNull()
                .first()

            loadDetailTuitionFee(semester)
        }
    }

    fun loadTotalTuitionFee(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = feeRepository.getTotalTuitionFee(isForceRefresh = forceRefresh)
            result.onSuccess { fee ->
                _totalTuitionFee.value = fee
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải tổng học phí"
            }
            _isLoading.value = false
        }
    }

    fun loadTuitionFeeSemester(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = feeRepository.getTuitionFeeSemester(isForceRefresh = forceRefresh)
            result.onSuccess {
                _tuitionFeeSemester.value = it
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun loadDetailTuitionFee(semester: Int, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = feeRepository.getDetailTuitionFee(
                semester = semester, isForceRefresh = forceRefresh
            )
            result.onSuccess {
                _detailTuitionFee.value = it
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun refreshTotalTuitionFee() {
        loadTotalTuitionFee(forceRefresh = true)
    }

    fun refreshTuitionFeeSemester() {
        loadTuitionFeeSemester(forceRefresh = true)
    }

    fun refreshDetailTuitionFee(semester: Int) {
        loadDetailTuitionFee(semester, forceRefresh = true)
    }
}
