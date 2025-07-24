package lamdx4.uis.ptithcm.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.local.LoginPrefs
import lamdx4.uis.ptithcm.data.model.CompleteStudentInfo
import lamdx4.uis.ptithcm.data.repository.GradeRepository
import lamdx4.uis.ptithcm.data.repository.ScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

data class AppUserState(
    val accessToken: String? = null,
    val maSV: String? = null,
    val username: String? = null,
    val password: String? = null,
    val rememberMe: Boolean = false,
    val profile: CompleteStudentInfo? = null
)

@HiltViewModel
class AppViewModel @Inject constructor(
    app: Application,
    private val gradeRepository: GradeRepository,
    private val scheduleRepository: ScheduleRepository
) : AndroidViewModel(app) {
    private val loginPrefs = LoginPrefs(app)

    private val _uiState = MutableStateFlow(AppUserState())
    val uiState: StateFlow<AppUserState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                loginPrefs.accessToken,
                loginPrefs.maSV,
                loginPrefs.username,
                loginPrefs.password,
                loginPrefs.rememberMe
            ) { accessToken, maSV, username, password, rememberMe ->
                AppUserState(
                    accessToken = accessToken,
                    maSV = maSV,
                    username = username,
                    password = password,
                    rememberMe = rememberMe
                )
            }.collect { _uiState.value = it }
        }
    }

    fun saveLoginInfo(
        accessToken: String,
        maSV: String,
        username: String,
        password: String,
        rememberMe: Boolean
    ) {
        viewModelScope.launch {
            // Clear profile and cache if logging in with a different account
            val currentMaSV = _uiState.value.maSV
            if (currentMaSV != null && currentMaSV != maSV) {
                _uiState.value = _uiState.value.copy(profile = null)
                // Clear repository caches to prevent showing data from previous account
                gradeRepository.clearCache()
                scheduleRepository.clearCache()
            }
            loginPrefs.saveLoginInfo(accessToken, maSV, username, password, rememberMe)
        }
    }

    fun clearLoginInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(profile = null)
            // Clear all repository caches when logging out
            gradeRepository.clearCache()
            scheduleRepository.clearCache()
            loginPrefs.clearLoginInfo()
        }
    }

    fun clearProfile() {
        _uiState.value = _uiState.value.copy(profile = null)
    }

    fun setProfile(profile: CompleteStudentInfo) {
        _uiState.value = _uiState.value.copy(profile = profile)
    }
}