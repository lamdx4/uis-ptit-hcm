package lamdx4.uis.ptithcm.data.model

data class ScheduleEvent(
    val title: String,
    val room: String,
    val startTime: String, // ISO 8601 String
    val endTime: String    // ISO 8601 String
)