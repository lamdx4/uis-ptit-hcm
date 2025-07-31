package lamdx4.uis.ptithcm.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.AcademicResultData
import lamdx4.uis.ptithcm.data.model.SemesterInfo
import lamdx4.uis.ptithcm.data.repository.StudentInfoRepository
import javax.inject.Inject

data class StatisticsUiState(
    val academicResult: AcademicResultData? = null,
    val availableSemesters: List<SemesterInfo> = emptyList(),
    val loading: Boolean = false,
    val loadingSemesters: Boolean = false,
    val error: String? = null,
    val selectedSemester: Int = 20242 // Học kỳ hiện tại mặc định
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val studentInfoRepository: StudentInfoRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState
    
    fun loadAvailableSemesters() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loadingSemesters = true)
            
            try {
                val response = studentInfoRepository.getAvailableSemesters()
                
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
    
    fun loadAcademicResult( hocKy: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            
            try {
                val response = studentInfoRepository.getAcademicResult( hocKy)
                
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
