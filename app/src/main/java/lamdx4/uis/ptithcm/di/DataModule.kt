package lamdx4.uis.ptithcm.di

import android.content.Context
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
    
    @Provides
    @Singleton
    fun provideLoginPrefs(@ApplicationContext context: Context): LoginPrefs {
        return LoginPrefs(context)
    }
}
