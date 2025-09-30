package lamdx4.uis.ptithcm.data.model

import androidx.room.Entity

@Entity(tableName = "schedule_items", primaryKeys = ["semesterCode", "studyDate", "startPeriod"])
data class ScheduleItemEntity(
    val semesterCode: Int,
    val studyDate: String,
    val startPeriod: Int,           // 1 = morning, 7 = afternoon
    val subjectName: String,
    val subjectCode: String,
    val roomCode: String?
)
