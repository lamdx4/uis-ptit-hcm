package lamdx4.uis.ptithcm.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import lamdx4.uis.ptithcm.data.model.AlarmEntity

@Database(entities = [AlarmEntity::class], version = 1, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}