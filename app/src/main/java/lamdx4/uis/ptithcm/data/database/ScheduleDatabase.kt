package lamdx4.uis.ptithcm.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import lamdx4.uis.ptithcm.data.model.ScheduleItemEntity

@Database(
    entities = [ScheduleItemEntity::class],
    version = 1
)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}
