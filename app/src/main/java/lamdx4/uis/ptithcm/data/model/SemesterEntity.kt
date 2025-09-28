package lamdx4.uis.ptithcm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "semesters")
data class SemesterEntity(
    @PrimaryKey val semesterCode: Int,
    val semesterName: String,
    val startDate: String, // yyyy-MM-dd
    val endDate: String    // yyyy-MM-dd
)
