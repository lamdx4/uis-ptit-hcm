package lamdx4.uis.ptithcm.ui.more.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.CourseItem
import lamdx4.uis.ptithcm.data.model.RegisterGroup
import lamdx4.uis.ptithcm.data.model.RegisteredSubject
import lamdx4.uis.ptithcm.data.model.SubjectFilter
import lamdx4.uis.ptithcm.data.repository.CourseRegistrationRepository
import javax.inject.Inject

data class CourseRegistrationUiState(
    val isLoading: Boolean = false,
    val availableSubjects: List<CourseItem> = emptyList(),
    val filteredSubjects: List<CourseItem> = emptyList(),
    val registeredSubjects: List<RegisteredSubject> = emptyList(),
    val subjectFilters: List<SubjectFilter> = emptyList(),
    val selectedFilter: SubjectFilter? = null,
    val minimumCredits: Int = 16,
    val error: String? = null,
    val successMessage: String? = null,
    val isInRegistrationTime: Boolean = false,
    val registrationNote: String = "",
    val searchQuery: String = ""
    ,val studentClass: String? = null
)

@HiltViewModel
class CourseRegistrationViewModel @Inject constructor(
    private val repository: CourseRegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseRegistrationUiState())
    val uiState: StateFlow<CourseRegistrationUiState> = _uiState.asStateFlow()


    fun initRegistration(studentClass: String?) {
        _uiState.value = _uiState.value.copy(studentClass = studentClass)
        loadFilters()
        loadAvailableSubjects()
        loadRegisteredSubjects()
    }

    fun loadFilters() {
        viewModelScope.launch {
            repository.getAllFilter().fold(
                onSuccess = { filters ->
                    val defaultFilter = filters.find { it.isDefault } ?: filters.firstOrNull()
                    _uiState.value = _uiState.value.copy(
                        subjectFilters = filters,
                        selectedFilter = defaultFilter
                    )
                    defaultFilter?.let { applyFilter(it) }
                },
                onFailure = { exception: Throwable ->
                    _uiState.value = _uiState.value.copy(
                        error = "Không thể tải bộ lọc: ${exception.message}"
                    )
                }
            )
        }
    }

    fun loadAvailableSubjects() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getAllSubjects().fold(
                onSuccess = { response ->
                    if (response.result) {
                        // Map RegisterGroup to CourseItem by matching subjectId/code
                        val subjectMap = response.data.subjects.associateBy { it.code }
                        val courseItems = response.data.groups.map { group ->
                            val subject = subjectMap[group.subjectCode]
                            CourseItem(group, subject)
                        }
                        _uiState.value = _uiState.value.copy(
                            availableSubjects = courseItems,
                            isInRegistrationTime = response.data.isInRegistrationTime,
                            registrationNote = response.data.registrationNote,
                            isLoading = false
                        )
                        // Apply current filter to new data
                        _uiState.value.selectedFilter?.let { filter ->
                            applyFilter(filter)
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = "Không thể tải danh sách môn học",
                            isLoading = false
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Lỗi kết nối mạng",
                        isLoading = false
                    )
                }
            )
        }
    }

    fun loadRegisteredSubjects() {
        viewModelScope.launch {
            repository.getAllRegisteredSubject().fold(
                onSuccess = { response ->
                    if (response.result) {
                        _uiState.value = _uiState.value.copy(
                            registeredSubjects = response.data.registeredSubjects,
                            minimumCredits = response.data.minimumCredits
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = "Không thể tải danh sách môn đã đăng ký"
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Lỗi kết nối mạng"
                    )
                }
            )
        }
    }

    fun selectFilter(filter: SubjectFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter, searchQuery = "")
        applyFilter(filter)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        _uiState.value.selectedFilter?.let { filter ->
            applyFilter(filter, searchQuery = query)
        }
    }

    // searchQuery chỉ dùng cho filter 10 (tìm kiếm theo mã môn học)
    private fun applyFilter(
        filter: SubjectFilter,
        searchQuery: String = _uiState.value.searchQuery
    ) {
        val allSubjects = _uiState.value.availableSubjects
        val studentClass = _uiState.value.studentClass
        val filteredSubjects = when (filter.value) {
            10 -> {
                if (searchQuery.isNotBlank()) {
                    allSubjects.filter {
                        it.group.subjectCode.contains(searchQuery, ignoreCase = true) ||
                        it.group.subjectName.contains(searchQuery, ignoreCase = true) ||
                        (it.subject?.name?.contains(searchQuery, ignoreCase = true) ?: false)
                    }
                } else allSubjects
            }
            4 -> {
                if (searchQuery.isNotBlank()) {
                    allSubjects.filter { item ->
                        item.group.classList.any { lop ->
                            lop.contains(searchQuery, ignoreCase = true)
                        }
                    }
                } else allSubjects
            }
            3 -> {
                if (searchQuery.isNotBlank()) {
                    allSubjects.filter {
                        it.group.facultyList.any { khoa -> khoa.contains(searchQuery, ignoreCase = true) }
                    }
                } else allSubjects
            }
            0 -> {
                if (!studentClass.isNullOrBlank()) {
                    allSubjects.filter { item ->
                        item.group.classList.contains(studentClass)
                    }
                } else allSubjects
            }
            2 -> allSubjects.filter { it.group.isCurriculumSubject }
            6 -> allSubjects.filter { it.group.isCurriculumSubject && !it.group.isCurriculumRequirement }
            1 -> allSubjects.filter { it.group.isRepeat }
            else -> allSubjects
        }
        _uiState.value = _uiState.value.copy(filteredSubjects = filteredSubjects)
    }

    fun registerSubject(subject: RegisterGroup) {
        viewModelScope.launch {
            if (!_uiState.value.isInRegistrationTime) {
                _uiState.value = _uiState.value.copy(
                    error = "Hiện tại không trong thời gian đăng ký môn học"
                )
                return@launch
            }

            if (!subject.isEnabled) {
                _uiState.value = _uiState.value.copy(
                    error = "Môn học này không thể đăng ký: ${subject.enableReason}"
                )
                return@launch
            }

            if (subject.remaining <= 0) {
                _uiState.value = _uiState.value.copy(
                    error = "Môn học ${subject.subjectName} đã hết chỗ"
                )
                return@launch
            }

            if (subject.isRegistered) {
                _uiState.value = _uiState.value.copy(
                    error = "Bạn đã đăng ký môn ${subject.subjectName} rồi"
                )
                return@launch
            }

            // TODO: Implement actual registration API call
            _uiState.value = _uiState.value.copy(
                successMessage = "Đăng ký môn ${subject.subjectName} thành công!"
            )

            // Reload data after registration
            refreshData()
        }
    }

    fun unregisterSubject(subject: RegisteredSubject) {
        viewModelScope.launch {
            if (!subject.canDelete) {
                _uiState.value = _uiState.value.copy(
                    error = "Không thể hủy môn ${subject.subjectGroup.subjectName}: ${subject.deleteReason}"
                )
                return@launch
            }

            if (subject.isWithdrawn) {
                _uiState.value = _uiState.value.copy(
                    error = "Môn ${subject.subjectGroup.subjectName} đã được rút trước đó"
                )
                return@launch
            }

            // TODO: Implement actual unregistration API call
            _uiState.value = _uiState.value.copy(
                successMessage = "Hủy đăng ký môn ${subject.subjectGroup.subjectName} thành công!"
            )

            // Reload data after unregistration
            refreshData()
        }
    }

    fun exportSchedule() {
        viewModelScope.launch {
            try {
                // TODO: Implement schedule export functionality
                _uiState.value = _uiState.value.copy(
                    successMessage = "Xuất lịch học thành công!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Không thể xuất lịch học: ${e.message}"
                )
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            error = null,
            successMessage = null
        )
    }

    fun refreshData() {
        loadAvailableSubjects()
        loadRegisteredSubjects()
    }
}
