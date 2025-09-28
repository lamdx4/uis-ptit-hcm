package lamdx4.uis.ptithcm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_items")
data class ScheduleItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    val semesterCode: Int,          // link to Semester table
    val studyDate: String,
    val startPeriod: Int,           // VD: 1 = morning, 7 = afternoon
    val subjectName: String,
    val room: String? = null
)
