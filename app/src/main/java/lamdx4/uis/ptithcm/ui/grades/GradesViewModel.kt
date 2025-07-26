package lamdx4.uis.ptithcm.ui.grades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.repository.GradeRepository
import lamdx4.uis.ptithcm.data.model.GradeStatistics
import lamdx4.uis.ptithcm.data.model.SemesterGrade
import lamdx4.uis.ptithcm.data.model.SubjectGrade
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class GradesUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val semesters: List<SemesterGrade> = emptyList(),
    val selectedSemester: SemesterGrade? = null,
    val selectedSubjects: List<SubjectGrade> = emptyList(),
    val statistics: GradeStatistics? = null
)

@HiltViewModel
class GradesViewModel @Inject constructor(
    private val gradeRepository: GradeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(GradesUiState())
    val uiState: StateFlow<GradesUiState> = _uiState.asStateFlow()

    fun loadGrades() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Lấy tất cả điểm
                val response = gradeRepository.getAllGrades()
                
                if (response.result) {
                    val semesters = response.data.semesterGrades
                    val statistics = gradeRepository.getGradeStatistics()
                    
                    // Chọn học kỳ mới nhất làm mặc định
                    val latestSemester = semesters
                        .filter { !it.semesterCode.startsWith("2025") } // Bỏ qua HK hiện tại chưa có điểm
                        .maxByOrNull { it.semesterCode.toIntOrNull() ?: 0 }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        semesters = semesters,
                        selectedSemester = latestSemester,
                        selectedSubjects = latestSemester?.subjectGrades ?: emptyList(),
                        statistics = statistics
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Không thể tải điểm số"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Lỗi không xác định"
                )
            }
        }
    }

    fun selectSemester(semester: SemesterGrade) {
        _uiState.value = _uiState.value.copy(
            selectedSemester = semester,
            selectedSubjects = semester.subjectGrades
        )
    }

    override fun onCleared() {
        super.onCleared()
        gradeRepository.close()
    }
}
