package lamdx4.uis.ptithcm.data.repository

import io.ktor.client.HttpClient
import lamdx4.uis.ptithcm.data.database.AlarmDao
import lamdx4.uis.ptithcm.data.model.AlarmEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
    private val client: HttpClient,
    private val alarmDao: AlarmDao
) : Cacheable {

    suspend fun insertAlarm(alarm: AlarmEntity) = alarmDao.insertAlarm(alarm)
    suspend fun updateAlarm(alarm: AlarmEntity) = alarmDao.updateAlarm(alarm)
    suspend fun deleteAlarm(alarm: AlarmEntity) = alarmDao.deleteAlarm(alarm)
    suspend fun getAllAlarms() = alarmDao.getAllAlarms()
    suspend fun getAlarmById(id: Int) = alarmDao.getAlarmById(id)

    suspend fun getExams() {

    }

    override fun clearCache() {
        TODO("Not yet implemented")
    }
}