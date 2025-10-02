package lamdx4.uis.ptithcm.receiver

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import lamdx4.uis.ptithcm.service.scheduleDailyWidgetUpdate
import lamdx4.uis.ptithcm.ui.widget.ScheduleWidget

class ScheduleWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ScheduleWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        // Khi widget lần đầu tiên được add vào màn hình
        scheduleDailyWidgetUpdate(context)
    }
}
