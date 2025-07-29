package lamdx4.uis.ptithcm.ui.more.curriculum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.CurriculumResponse
import lamdx4.uis.ptithcm.data.model.CurriculumTypeResponse
import lamdx4.uis.ptithcm.data.repository.CurriculumRepository
import javax.inject.Inject

@HiltViewModel
class CurriculumViewModel @Inject constructor(
    private val curriculumRepository: CurriculumRepository
) : ViewModel() {

    private val _curriculumTypeState = MutableStateFlow<List<CurriculumTypeResponse>>(emptyList())
    val curriculumTypeState = _curriculumTypeState

    private val _curriculumState = MutableStateFlow<CurriculumResponse?>(null)
    val curriculumState = _curriculumState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    fun loadCurriculumTypes() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = curriculumRepository.getCurriculumTypes()
            _isLoading.value = false

            result.onSuccess { types ->
                _curriculumTypeState.value = types
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải loại chương trình đào tạo"
            }
        }
    }

    fun loadCurriculums(programType: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val result = curriculumRepository.getCurriculum(programType)
            _isLoading.value = false

            result.onSuccess { curriculum ->
                _curriculumState.value = curriculum
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải chương trình đào tạo"
            }
        }
    }
}
