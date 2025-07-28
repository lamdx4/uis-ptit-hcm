package lamdx4.uis.ptithcm.ui.more.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.RegisterGroup
import lamdx4.uis.ptithcm.data.model.RegisteredSubject
import lamdx4.uis.ptithcm.data.model.SubjectFilter
import lamdx4.uis.ptithcm.data.repository.CourseRegistrationRepository
import javax.inject.Inject

data class CourseRegistrationUiState(
    val isLoading: Boolean = false,
    val availableSubjects: List<RegisterGroup> = emptyList(),
    val filteredSubjects: List<RegisterGroup> = emptyList(),
    val registeredSubjects: List<RegisteredSubject> = emptyList(),
    val subjectFilters: List<SubjectFilter> = emptyList(),
    val selectedFilter: SubjectFilter? = null,
    val minimumCredits: Int = 16,
    val error: String? = null,
    val successMessage: String? = null,
    val isInRegistrationTime: Boolean = false,
    val registrationNote: String = ""
)

@HiltViewModel
class CourseRegistrationViewModel @Inject constructor(
    private val repository: CourseRegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseRegistrationUiState())
    val uiState: StateFlow<CourseRegistrationUiState> = _uiState.asStateFlow()

    init {
        loadFilters()
        loadAvailableSubjects()
        loadRegisteredSubjects()
    }

    private fun loadFilters() {
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
                        _uiState.value = _uiState.value.copy(
                            availableSubjects = response.data.groups,
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
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        applyFilter(filter)
    }

    private fun applyFilter(filter: SubjectFilter) {
        val allSubjects = _uiState.value.availableSubjects
        val registeredSubjects = _uiState.value.registeredSubjects

        val filteredSubjects = when (filter.value) {
            0 -> {
                // "Môn học mở theo lớp sinh viên" - Tất cả môn mở
                allSubjects
            }
            1 -> {
                // "Môn sinh viên cần học lại (đã rớt)" - Môn học lại
                allSubjects.filter { it.isRepeat }
            }
            2 -> {
                // "Môn trong chương trình đào tạo kế hoạch" - Môn CTĐT
                allSubjects.filter { it.isCurriculumSubject }
            }
            3 -> {
                // "Lọc theo khoa quản lý môn học" - Theo khoa
                allSubjects // Cần thêm logic lọc theo khoa nếu có
            }
            4 -> {
                // "Lọc theo lớp" - Theo lớp
                allSubjects // Cần thêm logic lọc theo lớp nếu có
            }
            6 -> {
                // "Môn chưa học trong CTĐT kế hoạch" - Môn CTĐT chưa đăng ký
                val registeredSubjectIds = registeredSubjects.map { it.subjectGroup.subjectId }.toSet()
                allSubjects.filter {
                    it.isCurriculumSubject && !registeredSubjectIds.contains(it.subjectId)
                }
            }
            10 -> {
                // "Lọc theo môn học" - Tìm kiếm theo môn học
                allSubjects // Có thể thêm search functionality
            }
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
