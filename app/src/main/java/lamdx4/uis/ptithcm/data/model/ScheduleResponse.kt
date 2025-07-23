package lamdx4.uis.ptithcm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response from API w-locdstkbtuanusertheohocky 
 * For student weekly schedule
 */
@Serializable
data class ScheduleResponse(
    @SerialName("data")
    val data: ScheduleData,
    @SerialName("result")
    val result: Boolean,
    @SerialName("code")
    val code: Int
)

@Serializable
data class ScheduleData(
    @SerialName("total_items")
    val totalItems: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("ds_tiet_trong_ngay")
    val periodsInDay: List<PeriodInfo>,
    @SerialName("ds_tuan_tkb")
    val weeklySchedules: List<WeeklySchedule>,
    @SerialName("is_duoc_diem_danh")
    val canTakeAttendance: Boolean,
    @SerialName("is_duoc_dk_nghi_day")
    val canRegisterAbsence: Boolean,
    @SerialName("is_quan_ly_hoc_lieu")
    val canManageMaterials: Boolean,
    @SerialName("is_show_tiet")
    val showPeriod: Boolean
)

@Serializable
data class PeriodInfo(
    @SerialName("tiet")
    val period: Int,
    @SerialName("gio_bat_dau")
    val startTime: String = "",
    @SerialName("gio_ket_thuc")
    val endTime: String = "",
    @SerialName("so_phut")
    val durationMinutes: Int = 0,
    @SerialName("nhhk")
    val semesterCode: Int
)

@Serializable
data class WeeklySchedule(
    @SerialName("tuan_hoc_ky")
    val semesterWeek: Int? = null,
    @SerialName("tuan_tuyet_doi")
    val absoluteWeek: Int? = null,
    @SerialName("thong_tin_tuan")
    val weekInfo: String? = null,
    @SerialName("ngay_bat_dau")
    val startDate: String? = null,
    @SerialName("ngay_ket_thuc")
    val endDate: String? = null,
    @SerialName("ds_thoi_khoa_bieu")
    val scheduleItems: List<ScheduleItem>,
    @SerialName("ds_id_thoi_khoa_bieu_trung")
    val conflictingScheduleIds: List<String>
)

@Serializable
data class ScheduleItem(
    @SerialName("is_hk_lien_truoc")
    val isPreviousSemester: Int? = null,
    @SerialName("thu_kieu_so")
    val dayOfWeek: Int? = null, // 2=Monday, 3=Tuesday, etc.
    @SerialName("tiet_bat_dau")
    val startPeriod: Int? = null,
    @SerialName("so_tiet")
    val numberOfPeriods: Int? = null,
    @SerialName("ma_mon")
    val subjectCode: String = "",
    @SerialName("ten_mon")
    val subjectName: String = "",
    @SerialName("so_tin_chi")
    val credits: String = "",
    @SerialName("id_to_hoc")
    val classId: String = "",
    @SerialName("id_tkb")
    val scheduleId: String = "",
    @SerialName("id_to_hop")
    val groupId: String = "",
    @SerialName("ma_nhom")
    val groupCode: String = "",
    @SerialName("ma_to_th")
    val subGroupCode: String = "",
    @SerialName("ma_to_hop")
    val combinedGroupCode: String = "",
    @SerialName("ma_giang_vien")
    val teacherCode: String = "",
    @SerialName("ten_giang_vien")
    val teacherName: String = "",
    @SerialName("ma_lop")
    val classCode: String = "", 
    @SerialName("ma_phong")
    val roomCode: String = "",
    @SerialName("ma_co_so")
    val campusCode: String = "",
    @SerialName("ngay_hoc")
    val studyDate: String? = null,
    @SerialName("id_tu_tao")
    val selfCreatedId: String = "",
    @SerialName("is_file_bai_giang")
    val hasLectureMaterial: Boolean = false,
    @SerialName("id_sinh_hoat")
    val activityId: String = ""
)

// Helper data class for easier UI handling
data class DaySchedule(
    val dayOfWeek: Int, // 2=Monday, 3=Tuesday, etc.
    val dayName: String,
    val date: String,
    val scheduleItems: List<ScheduleItem>
)

data class WeekScheduleDisplay(
    val weekInfo: String,
    val startDate: String,
    val endDate: String,
    val daySchedules: List<DaySchedule>
)
