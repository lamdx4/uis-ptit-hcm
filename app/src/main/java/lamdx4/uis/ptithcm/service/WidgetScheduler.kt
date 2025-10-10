package lamdx4.uis.ptithcm.service

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleDailyWidgetUpdate(context: Context) {

    val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
        4, TimeUnit.HOURS // lặp lại mỗi 4 giờ
    )
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "daily_widget_update",                 // tên job duy nhất
        ExistingPeriodicWorkPolicy.UPDATE,     // nếu đã có thì thay bằng job mới
        workRequest
    )
}
