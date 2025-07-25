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
    val curriculumTypeState = _curriculumTypeState // public để UI collect

    private val _curriculumState = MutableStateFlow<CurriculumResponse?>(null)
    val curriculumState = _curriculumState // public để UI collect

    fun loadCurriculumTypes(accessToken: String) {
        viewModelScope.launch {
            try {
                _curriculumTypeState.value = curriculumRepository.getCurriculumTypes(accessToken)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun  loadCurriculums(accessToken: String, programType: Int) {
        viewModelScope.launch {
            try {
                _curriculumState.value = curriculumRepository.getCurriculum(accessToken, programType)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}