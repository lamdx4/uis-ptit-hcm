package lamdx4.uis.ptithcm.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.local.LoginPrefs
import lamdx4.uis.ptithcm.data.model.CompleteStudentInfo

data class AppUserState(
    val accessToken: String? = null,
    val maSV: String? = null,
    val username: String? = null,
    val password: String? = null,
    val rememberMe: Boolean = false,
    val profile: CompleteStudentInfo? = null
)

class AppViewModel(app: Application) : AndroidViewModel(app) {
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
            loginPrefs.saveLoginInfo(accessToken, maSV, username, password, rememberMe)
        }
    }

    fun clearLoginInfo() {
        viewModelScope.launch {
            loginPrefs.clearLoginInfo()
        }
    }

    fun setProfile(profile: CompleteStudentInfo) {
        _uiState.value = _uiState.value.copy(profile = profile)
    }
}