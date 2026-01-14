package lamdx4.uis.ptithcm.ui.more.prerequisites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.PrerequisiteResponse
import lamdx4.uis.ptithcm.data.model.PrerequisitesTypeResponse
import lamdx4.uis.ptithcm.data.repository.PrerequisiteRepository
import javax.inject.Inject

@HiltViewModel
class PrerequisitesViewModel @Inject constructor(
    private val prerequisiteRepository: PrerequisiteRepository
) : ViewModel() {
    private val _prerequisites = MutableStateFlow<PrerequisiteResponse?>(null)
    val prerequisites = _prerequisites

    private val _prerequisiteType = MutableStateFlow<List<PrerequisitesTypeResponse>>(emptyList())
    val prerequisiteType = _prerequisiteType

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    init {
        viewModelScope.launch {
            loadPrerequisites(1)
            loadPrerequisiteTypes()
        }
    }

    fun loadPrerequisites(prerequisiteType: Int = 1, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = prerequisiteRepository.getPrerequisites(
                prerequisiteType = prerequisiteType,
                isForceRefresh = forceRefresh
            )
            result.onSuccess {
                _prerequisites.value = it
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải danh sách môn học"
            }
            _isLoading.value = false
        }
    }

    fun loadPrerequisiteTypes(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = prerequisiteRepository.getPrerequisiteType(isForceRefresh = forceRefresh)
            result.onSuccess { fee ->
                prerequisiteType.value = fee
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải danh sách môn học"
            }
            _isLoading.value = false
        }
    }

    fun refreshPrerequisites(prerequisiteType: Int = 1) {
        loadPrerequisites(prerequisiteType, forceRefresh = true)
    }

    fun refreshPrerequisiteTypes() {
        loadPrerequisiteTypes(forceRefresh = true)
    }
}
