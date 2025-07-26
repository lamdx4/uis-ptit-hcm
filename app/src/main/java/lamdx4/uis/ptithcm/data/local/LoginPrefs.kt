// lamdx4/uis/ptithcm/data/local/LoginPrefs.kt
package lamdx4.uis.ptithcm.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.loginDataStore

    private var cachedAccessToken: String? = null
    private var cachedRefreshToken: String? = null

    val accessToken: Flow<String?> = dataStore.data.map { it[LoginPrefsKeys.ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = dataStore.data.map { it[LoginPrefsKeys.REFRESH_TOKEN] }
    val maSV: Flow<String?> = dataStore.data.map { it[LoginPrefsKeys.MASV] }
    val username: Flow<String?> = dataStore.data.map { it[LoginPrefsKeys.USERNAME] }
    val password: Flow<String?> = dataStore.data.map { it[LoginPrefsKeys.PASSWORD] }
    val rememberMe: Flow<Boolean> = dataStore.data.map { it[LoginPrefsKeys.REMEMBER_ME] ?: false }

    suspend fun saveLoginInfo(
        accessToken: String,
        refreshToken: String,
        maSV: String,
        username: String,
        password: String,
        rememberMe: Boolean
    ) {
        Log.d("LoginPrefs", "Saving: username=$username, remember=$rememberMe")
        cachedAccessToken = accessToken
        cachedRefreshToken = refreshToken
        dataStore.edit { prefs ->
            prefs[LoginPrefsKeys.ACCESS_TOKEN] = accessToken
            prefs[LoginPrefsKeys.REFRESH_TOKEN] = refreshToken
            prefs[LoginPrefsKeys.MASV] = maSV
            prefs[LoginPrefsKeys.USERNAME] = if (rememberMe) username else ""
            prefs[LoginPrefsKeys.PASSWORD] = if (rememberMe) password else ""
            prefs[LoginPrefsKeys.REMEMBER_ME] = rememberMe
        }
    }

    suspend fun clearLoginInfo() {
        dataStore.edit { it.clear() }
    }

    suspend fun getAccessToken(): String? {
        if (cachedAccessToken != null) return cachedAccessToken
        return accessToken.firstOrNull()
    }

    suspend fun getRefreshToken(): String? {
        if (cachedRefreshToken != null) return cachedRefreshToken
        return refreshToken.firstOrNull()
    }

    suspend fun saveAccessToken(accessToken: String) {
        cachedAccessToken = accessToken
        dataStore.edit { it[LoginPrefsKeys.ACCESS_TOKEN] = accessToken }
    }
}
