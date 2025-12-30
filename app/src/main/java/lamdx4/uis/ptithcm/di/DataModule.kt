package lamdx4.uis.ptithcm.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import lamdx4.uis.ptithcm.data.local.LoginPrefs
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    private const val DATA_STORE_NAME = "ptit_app_datastore"
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<androidx.datastore.preferences.core.Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(DATA_STORE_NAME) }
        )
    }
    
    @Provides
    @Singleton
    fun provideLoginPrefs(@ApplicationContext context: Context): LoginPrefs {
        return LoginPrefs(context)
    }
}
