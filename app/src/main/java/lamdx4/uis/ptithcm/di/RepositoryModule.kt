package lamdx4.uis.ptithcm.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import lamdx4.uis.ptithcm.data.repository.ScheduleRepository
import lamdx4.uis.ptithcm.data.repository.GradeRepository
import lamdx4.uis.ptithcm.data.repository.StudentRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideScheduleRepository(): ScheduleRepository {
        return ScheduleRepository()
    }

    @Provides
    @Singleton
    fun provideGradeRepository(): GradeRepository {
        return GradeRepository()
    }

    @Provides
    @Singleton
    fun provideStudentRepository(): StudentRepository {
        return StudentRepository()
    }
}
