package lamdx4.uis.ptithcm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var time: Long, // Milliseconds for easy comparison
    val label: String,
    val isEnabled: Boolean = true,
    val toneUri: String?,
    val vibrate: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
