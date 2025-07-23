package lamdx4.uis.ptithcm.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.repository.StudentRepository
import lamdx4.uis.ptithcm.data.model.AcademicResultData
import lamdx4.uis.ptithcm.data.model.GradeStatistics
import lamdx4.uis.ptithcm.data.model.SemesterInfo

data class StatisticsUiState(
    val academicResult: AcademicResultData? = null,
    val availableSemesters: List<SemesterInfo> = emptyList(),
    val loading: Boolean = false,
    val loadingSemesters: Boolean = false,
    val error: String? = null,
    val selectedSemester: Int = 20242 // Học kỳ hiện tại mặc định
)

class StatisticsViewModel(
    private val studentRepository: StudentRepository = StudentRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState
    
    fun loadAvailableSemesters(accessToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loadingSemesters = true)
            
            try {
                val response = studentRepository.getAvailableSemesters(accessToken)
                
                if (response.result && response.data?.ds_hoc_ky != null) {
                    val semesters = response.data.ds_hoc_ky.sortedByDescending { it.hoc_ky }
                    _uiState.value = _uiState.value.copy(
                        availableSemesters = semesters,
                        loadingSemesters = false,
                        selectedSemester = semesters.firstOrNull()?.hoc_ky ?: 20242
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        loadingSemesters = false,
                        error = "Không thể tải danh sách học kỳ"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loadingSemesters = false,
                    error = "Lỗi khi tải danh sách học kỳ: ${e.message}"
                )
            }
        }
    }
    
    fun loadAcademicResult(accessToken: String, hocKy: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            
            try {
                val response = studentRepository.getAcademicResult(accessToken, hocKy)
                
                if (response.result) {
                    _uiState.value = _uiState.value.copy(
                        academicResult = response.data,
                        loading = false,
                        selectedSemester = hocKy
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = "Không thể tải kết quả học tập"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = "Lỗi: ${e.message}"
                )
            }
        }
    }
    
    fun setSemester(hocKy: Int) {
        _uiState.value = _uiState.value.copy(selectedSemester = hocKy)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
