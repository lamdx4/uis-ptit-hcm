package lamdx4.uis.ptithcm.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import lamdx4.uis.ptithcm.data.model.ScheduleItemEntity

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ScheduleItemEntity>)

    @Query("SELECT * FROM schedule_items WHERE studyDate = :date AND startPeriod = :period LIMIT 1")
    suspend fun getClassByDateAndPeriod(date: String, period: Int): ScheduleItemEntity?

    @Query("SELECT * FROM schedule_items WHERE studyDate = :date")
    suspend fun getClassesByDate(date: String): List<ScheduleItemEntity>
}
