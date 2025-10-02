package lamdx4.uis.ptithcm.service

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import lamdx4.uis.ptithcm.util.calculateInitialDelay
import java.util.concurrent.TimeUnit

fun scheduleDailyWidgetUpdate(context: Context) {
    val delay = calculateInitialDelay() // tính số ms từ bây giờ đến 0h hôm sau

    val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
        1, TimeUnit.DAYS // lặp lại mỗi 1 ngày
    )
        .setInitialDelay(delay, TimeUnit.MILLISECONDS) // lần đầu chạy sau delay
        .build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "daily_widget_update",                 // tên job duy nhất
        ExistingPeriodicWorkPolicy.UPDATE,     // nếu đã có thì thay bằng job mới
        workRequest
    )
}
