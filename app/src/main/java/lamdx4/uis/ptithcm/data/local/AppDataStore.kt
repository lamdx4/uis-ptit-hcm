package lamdx4.uis.ptithcm.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.loginDataStore: androidx.datastore.core.DataStore<Preferences> by preferencesDataStore(
    name = "login_prefs"
)
