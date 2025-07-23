package lamdx4.uis.ptithcm.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    // 🎯 All repositories now use @Inject constructor() and @Singleton annotation
    // Hilt automatically provides them without manual @Provides methods!
    
    // No need for @Provides when using @Inject constructor() + @Singleton:
    // - ScheduleRepository: @Singleton class + @Inject constructor() ✅
    // - GradeRepository: @Singleton class + @Inject constructor() ✅  
    // - StudentRepository: @Singleton class + @Inject constructor() ✅
}
