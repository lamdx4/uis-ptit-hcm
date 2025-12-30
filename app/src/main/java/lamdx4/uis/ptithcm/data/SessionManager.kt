package lamdx4.uis.ptithcm.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import lamdx4.uis.ptithcm.data.repository.Cacheable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val cacheables: Set<@JvmSuppressWildcards Cacheable>,
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        // Định nghĩa Key để lưu Cookie
        val COOKIE_KEY = stringPreferencesKey("auth_cookie")
    }

    val cookieFlow: Flow<String> = dataStore.data.map { preferences ->
        preferences[COOKIE_KEY] ?: ""
    }

    suspend fun saveCookie(cookie: String) {
        dataStore.edit { preferences ->
            preferences[COOKIE_KEY] = cookie
        }
    }

    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(COOKIE_KEY)
        }
    }

    fun logout() {
        cacheables.forEach { it.clearCache() }
    }
}