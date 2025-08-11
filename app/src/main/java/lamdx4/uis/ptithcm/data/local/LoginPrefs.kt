package lamdx4.uis.ptithcm.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import lamdx4.uis.ptithcm.data.repository.Cacheable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginPrefs @Inject constructor(
    @param:ApplicationContext private val context: Context
) : Cacheable {

    private val dataStore = context.loginDataStore

    private var cachedAccessToken: String? = null
    private var cachedRefreshToken: String? = null

    var studentId: String? = null
    val username: Flow<String?> = dataStore.data.map { it[LoginPrefsKeys.USERNAME] }
    val password: Flow<String?> = dataStore.data.map { it[LoginPrefsKeys.PASSWORD] }
    val rememberMe: Flow<Boolean> = dataStore.data.map { it[LoginPrefsKeys.REMEMBER_ME] ?: false }

    suspend fun saveLoginInfo(
        accessToken: String,
        refreshToken: String,
        studentId: String,
        username: String,
        password: String,
        rememberMe: Boolean
    ) {
        cachedAccessToken = accessToken
        cachedRefreshToken = refreshToken
        this.studentId = studentId
        dataStore.edit { prefs ->
            prefs[LoginPrefsKeys.USERNAME] = if (rememberMe) username else ""
            prefs[LoginPrefsKeys.PASSWORD] = if (rememberMe) password else ""
            prefs[LoginPrefsKeys.REMEMBER_ME] = rememberMe
        }
    }


    fun getAccessToken(): String? {
        return cachedAccessToken
    }

    fun getRefreshToken(): String? {
        return cachedRefreshToken
    }

    fun saveAccessToken(accessToken: String) {
        cachedAccessToken = accessToken
    }

    suspend fun deleteAllDataLogin(){
        cachedAccessToken = null
        cachedRefreshToken = null
        this.studentId = null
        dataStore.edit { prefs ->
            prefs[LoginPrefsKeys.USERNAME] =  ""
            prefs[LoginPrefsKeys.PASSWORD] = ""
            prefs[LoginPrefsKeys.REMEMBER_ME] = false
        }
    }

    override fun clearCache() {
        cachedAccessToken = null
        cachedRefreshToken = null
        studentId = null
    }
}
