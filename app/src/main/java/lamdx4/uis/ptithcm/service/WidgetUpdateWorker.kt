package lamdx4.uis.ptithcm.service

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import lamdx4.uis.ptithcm.ui.widget.ScheduleWidget

class WidgetUpdateWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // refresh token lifetime is too short
//            // Lấy repository từ Hilt
//            val entryPoint = EntryPointAccessors.fromApplication(
//                context,
//                ScheduleRepositoryEntryPoint::class.java
//            )
//            val scheduleRepository = entryPoint.getScheduleRepository()
//
//            // Gọi API lấy học kỳ và lưu DB
//            val semesterCode = scheduleRepository.getCurrentSemester()?.semesterCode
//            if (semesterCode != null) {
//                scheduleRepository.saveWeeklySchedule(semesterCode)
//            }

            // Cập nhật widget
            ScheduleWidget().updateAll(context)

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}