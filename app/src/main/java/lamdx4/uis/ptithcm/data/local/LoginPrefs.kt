package lamdx4.uis.ptithcm.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("login_prefs")

object LoginPrefsKeys {
    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val MASV = stringPreferencesKey("ma_sv")
    val USERNAME = stringPreferencesKey("username")
    val PASSWORD = stringPreferencesKey("password")
    val REMEMBER_ME = booleanPreferencesKey("remember_me")
}

class LoginPrefs(private val context: Context) {
    val accessToken: Flow<String?> = context.dataStore.data.map { it[LoginPrefsKeys.ACCESS_TOKEN] }
    val maSV: Flow<String?> = context.dataStore.data.map { it[LoginPrefsKeys.MASV] }
    val username: Flow<String?> = context.dataStore.data.map { it[LoginPrefsKeys.USERNAME] }
    val password: Flow<String?> = context.dataStore.data.map { it[LoginPrefsKeys.PASSWORD] }
    val rememberMe: Flow<Boolean> = context.dataStore.data.map { it[LoginPrefsKeys.REMEMBER_ME] ?: false }

    suspend fun saveLoginInfo(
        accessToken: String,
        maSV: String,
        username: String,
        password: String,
        rememberMe: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[LoginPrefsKeys.ACCESS_TOKEN] = accessToken
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
}