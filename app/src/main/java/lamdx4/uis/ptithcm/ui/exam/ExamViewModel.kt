package lamdx4.uis.ptithcm.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.model.AlarmEntity
import lamdx4.uis.ptithcm.data.model.ExamResponse
import lamdx4.uis.ptithcm.data.model.ExamSemesterResponse
import lamdx4.uis.ptithcm.data.model.ExamSubTypeResponse
import lamdx4.uis.ptithcm.data.model.ExamTypeResponse
import lamdx4.uis.ptithcm.data.model.SubTypeExamResponse
import lamdx4.uis.ptithcm.data.repository.ExamRepository
import javax.inject.Inject

@HiltViewModel
class ExamViewModel @Inject constructor(
    private val examRepository: ExamRepository
) : ViewModel() {
    private val _alarms = mutableListOf<AlarmEntity>()
    val alarms = _alarms

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

    fun loadAlarms() {
        viewModelScope.launch {
            _alarms.clear()
            _alarms.addAll(examRepository.getAllAlarms())
        }
    }

    fun addAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            examRepository.insertAlarm(alarm)
            loadAlarms()
        }
    }

    fun updateAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            examRepository.updateAlarm(alarm)
            loadAlarms()
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            examRepository.deleteAlarm(alarm)
            loadAlarms()
        }
    }

    fun loadPersonalExams(semester: Int, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = examRepository.getPersonalExams(semester, forceRefresh)
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
            val result = examRepository.getExamSemesters(forceRefresh)
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
            val result = examRepository.getExamTypes(forceRefresh)
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
            val result = examRepository.getExamSubTypes(semester, examType, forceRefresh)
            result.onSuccess { subTypes ->
                _examSubTypeState.value = subTypes
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải bộ lọc loại thi"
            }
            _isLoading.value = false
        }
    }

    fun loadSubTypeExams(semester: Int, examType: Int, subType: String, examDate: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = examRepository.getSubTypeExams(semester, examType, subType, examDate, forceRefresh)
            result.onSuccess { exams ->
                _subTypeExamState.value = exams
                _errorMessage.value = null
            }.onFailure { e ->
                _errorMessage.value = e.message ?: "Không thể tải lịch thi"
            }
            _isLoading.value = false
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
