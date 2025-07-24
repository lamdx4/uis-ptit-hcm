package lamdx4.uis.ptithcm.ui.more.curriculum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.EducationProgramData
import lamdx4.uis.ptithcm.data.model.EducationProgramType
import lamdx4.uis.ptithcm.data.repository.CurriculumRepository
import javax.inject.Inject

@HiltViewModel
class CurriculumViewModel @Inject constructor(
    private val curriculumRepository: CurriculumRepository
) : ViewModel() {
    private val _curriculumTypeState = MutableStateFlow<List<EducationProgramType>>(emptyList())
    private val _curriculumState = MutableStateFlow(EducationProgramData)

    suspend fun loadCurriculumTypes(accessToken: String) {
        viewModelScope.launch {
            _curriculumTypeState.value = curriculumRepository.getEducationProgramTypes(accessToken)
            println("loadCurriculumTypes: ${_curriculumTypeState.value}")
        }
    }
}