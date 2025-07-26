package lamdx4.uis.ptithcm.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("login_prefs")

object LoginPrefsKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token") // ✅ new
    val MASV = stringPreferencesKey("ma_sv")
    val USERNAME = stringPreferencesKey("username")
    val PASSWORD = stringPreferencesKey("password")
    val REMEMBER_ME = booleanPreferencesKey("remember_me")
}

@Singleton
class LoginPrefs @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private var cachedAccessToken: String? = null
    private var cachedRefreshToken: String? = null

    private val accessToken: Flow<String?> = context.dataStore.data.map { it[LoginPrefsKeys.ACCESS_TOKEN] }
    private val refreshToken: Flow<String?> = context.dataStore.data.map { it[LoginPrefsKeys.REFRESH_TOKEN] } // ✅ new
    val maSV: Flow<String?> = context.dataStore.data.map { it[LoginPrefsKeys.MASV] }
    val username: Flow<String?> = context.dataStore.data.map { it[LoginPrefsKeys.USERNAME] }
    val password: Flow<String?> = context.dataStore.data.map { it[LoginPrefsKeys.PASSWORD] }
    val rememberMe: Flow<Boolean> = context.dataStore.data.map { it[LoginPrefsKeys.REMEMBER_ME] ?: false }

    suspend fun saveLoginInfo(
        accessToken: String,
        refreshToken: String, // ✅ new param
        maSV: String,
        username: String,
        password: String,
        rememberMe: Boolean
    ) {
        cachedAccessToken = accessToken
        cachedRefreshToken = refreshToken
        context.dataStore.edit { prefs ->
            prefs[LoginPrefsKeys.ACCESS_TOKEN] = accessToken
            prefs[LoginPrefsKeys.REFRESH_TOKEN] = refreshToken // ✅
            prefs[LoginPrefsKeys.MASV] = maSV
            prefs[LoginPrefsKeys.USERNAME] = if (rememberMe) username else ""
            prefs[LoginPrefsKeys.PASSWORD] = if (rememberMe) password else ""
            prefs[LoginPrefsKeys.REMEMBER_ME] = rememberMe
        }
    }

    suspend fun clearLoginInfo() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
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
        this.cachedAccessToken = accessToken
        context.dataStore.edit {
            prefs -> prefs[LoginPrefsKeys.ACCESS_TOKEN] = accessToken
        }
    }
}
