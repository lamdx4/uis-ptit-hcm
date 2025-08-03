package lamdx4.uis.ptithcm.data.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import lamdx4.uis.ptithcm.data.local.LoginPrefs

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @IntoSet
    abstract fun bindAuthRepository(repo: AuthRepository): Cacheable

    @Binds
    @IntoSet
    abstract fun bindCourseRegistrationRepository(repo: CourseRegistrationRepository): Cacheable

    @Binds
    @IntoSet
    abstract fun bindCurriculumRepository(repo: CurriculumRepository): Cacheable

    @Binds
    @IntoSet
    abstract fun bindGradeRepository(repo: GradeRepository): Cacheable

    @Binds
    @IntoSet
    abstract fun bindPaymentRepository(repo: PaymentRepository): Cacheable

    @Binds
    @IntoSet
    abstract fun bindScheduleRepository(repo: ScheduleRepository): Cacheable

    @Binds
    @IntoSet
    abstract fun bindStudentInfoRepository(repo: StudentInfoRepository): Cacheable

    @Binds
    @IntoSet
    abstract fun bindCalendarSyncRepository(repo: CalendarSyncRepository): Cacheable

    @Binds
    @IntoSet
    abstract fun bindLoginPrefs(prefs: LoginPrefs): Cacheable
}
