package lamdx4.uis.ptithcm.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.AlarmEntity
import lamdx4.uis.ptithcm.data.model.ExamResponse
import lamdx4.uis.ptithcm.data.model.ExamSemesterResponse
import lamdx4.uis.ptithcm.data.model.ExamSubTypeResponse
import lamdx4.uis.ptithcm.data.model.ExamTypeResponse
import lamdx4.uis.ptithcm.data.model.SubTypeExamResponse
import lamdx4.uis.ptithcm.data.repository.ExamRepository
import lamdx4.uis.ptithcm.data.repository.ExamUiState
import javax.inject.Inject

@HiltViewModel
class ExamViewModel @Inject constructor(
    private val examRepository: ExamRepository,
) : ViewModel() {
    private val _alarms = MutableStateFlow<List<AlarmEntity>>(emptyList())
    val alarms: StateFlow<List<AlarmEntity>> = _alarms.asStateFlow()

    private val _personalExamState = MutableStateFlow<ExamResponse?>(null)
    val personalExamState = _personalExamState

    private val _examSemesterState = MutableStateFlow<ExamSemesterResponse?>(null)
    val examSemesterState = _examSemesterState

    private val _examTypeState = MutableStateFlow<ExamTypeResponse?>(null)
    val examTypeState = _examTypeState

    private val _examSubTypeState = MutableStateFlow<ExamSubTypeResponse?>(null)
    val examSubTypeState = _examSubTypeState

    private val _subTypeExamState = MutableStateFlow<SubTypeExamResponse?>(null)
    val subTypeExamState = _subTypeExamState

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading

    private val _uiState = MutableStateFlow(
        examRepository.getUiState() ?: ExamUiState()
    )
    val uiState: StateFlow<ExamUiState> = _uiState.asStateFlow()

    fun setSelectedSemester(semester: Int) {
        _uiState.value = _uiState.value.copy(selectedSemester = semester)
        examRepository.saveUiState(_uiState.value)
    }

    fun setSelectedType(type: Int) {
        _uiState.value = _uiState.value.copy(selectedType = type)
        examRepository.saveUiState(_uiState.value)
    }

    fun setSelectedSubType(subType: String?) {
        _uiState.value = _uiState.value.copy(selectedSubType = subType)
        examRepository.saveUiState(_uiState.value)
    }

    fun setSelectedDate(date: String) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        examRepository.saveUiState(_uiState.value)
    }

    fun loadAlarms() {
        viewModelScope.launch {
            _alarms.value = examRepository.getAllAlarms()
        }
    }

    fun addAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            examRepository.insertAlarm(alarm)
            _alarms.value = examRepository.getAllAlarms()
        }
    }

    fun updateAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            examRepository.updateAlarm(alarm)
            _alarms.value = examRepository.getAllAlarms()
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            examRepository.deleteAlarm(alarm)
            _alarms.value = examRepository.getAllAlarms()
        }
    }

    init {
        viewModelScope.launch {
            loadExamSemesters()
            val semesterCode = examSemesterState
                .filterNotNull()
                .map { it.data.semesters.firstOrNull()?.semesterCode }
                .filterNotNull()
                .first()

            loadPersonalExams(semesterCode)

            loadExamTypes()
            val typeId = examTypeState
                .filterNotNull()
                .map { it.data.scheduleObjects.getOrNull(1)?.objectType }
                .filterNotNull()
                .first()

            loadExamSubTypes(semesterCode, typeId)
            val subTypeId = examSubTypeState
                .filterNotNull()
                .map { it.data.dataItems.firstOrNull()?.dataId }
                .filterNotNull()
                .first()

            loadSubTypeExams(
                semesterCode,
                examTypeState.value?.data?.scheduleObjects[1]?.objectType ?: 3,
                subTypeId,
                ""
            )

            loadAlarms()
            loadUiState(false) // Load UI state from cache
        }
    }

    fun loadPersonalExams(semester: Int, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result =
                examRepository.getPersonalExams(semester = semester, isForceRefresh = forceRefresh)
            result.onSuccess { exams ->
                _personalExamState.value = exams
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải lịch thi cá nhân"
            }
            _isLoading.value = false
        }
    }

    fun loadExamSemesters(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = examRepository.getExamSemesters(isForceRefresh = forceRefresh)
            result.onSuccess { semesters ->
                _examSemesterState.value = semesters
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải kỳ thi"
            }
            _isLoading.value = false
        }
    }

    fun loadExamTypes(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = examRepository.getExamTypes(isForceRefresh = forceRefresh)
            result.onSuccess { types ->
                _examTypeState.value = types
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải loại thi"
            }
            _isLoading.value = false
        }
    }

    fun loadExamSubTypes(semester: Int, examType: Int, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = examRepository.getExamSubTypes(
                semester = semester,
                examType = examType,
                isForceRefresh = forceRefresh
            )
            result.onSuccess { subTypes ->
                _examSubTypeState.value = subTypes
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải bộ lọc loại thi"
            }
            _isLoading.value = false
        }
    }

    fun loadSubTypeExams(
        semester: Int,
        examType: Int,
        subType: String,
        examDate: String,
        forceRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val result =
                examRepository.getSubTypeExams(
                    semester = semester,
                    examType = examType,
                    subType = subType,
                    examDate = examDate,
                    isForceRefresh = forceRefresh
                )
            result.onSuccess { exams ->
                _subTypeExamState.value = exams
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải lịch thi"
            }
            _isLoading.value = false
        }
    }

    fun loadUiState(forceRefresh: Boolean = false) {
        if (forceRefresh || (_uiState.value.selectedSemester == null || _uiState.value.selectedType == null)) {
            _uiState.value = _uiState.value.copy(
                selectedSemester = examSemesterState.value?.data?.semesters?.first()?.semesterCode
                    ?: 20243,
                selectedType = examTypeState.value?.data?.scheduleObjects[0]?.objectType ?: 1,
                selectedSubType = null,
                selectedDate = null
            )
            examRepository.saveUiState(_uiState.value)
        }
    }

    fun refreshPersonalExams(semester: Int) {
        loadPersonalExams(semester, forceRefresh = true)
    }

    fun refreshExamSemesters() {
        loadExamSemesters(forceRefresh = true)
    }

    fun refreshExamTypes() {
        loadExamTypes(forceRefresh = true)
    }

    fun refreshExamSubTypes(semester: Int, examType: Int) {
        loadExamSubTypes(semester, examType, forceRefresh = true)
    }

    fun refreshSubTypeExams(semester: Int, examType: Int, subType: String, examDate: String) {
        loadSubTypeExams(semester, examType, subType, examDate, forceRefresh = true)
    }
}
