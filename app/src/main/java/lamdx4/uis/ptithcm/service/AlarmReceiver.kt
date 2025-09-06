package lamdx4.uis.ptithcm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val label = intent?.getStringExtra("label")
        val requestCode = intent?.getIntExtra("requestCode", -1) ?: -1

        if (intent?.action == "alarm.ACTION_DISMISS") {
            context?.stopService(Intent(context, AlarmForegroundService::class.java))
            return
        } else if (intent?.action == "alarm.ACTION_SNOOZE") {
            return
        }

        val alarmForegroundIntent = Intent(context, AlarmForegroundService::class.java).apply {
            putExtra("requestCode", requestCode)
            putExtra("label", label)
        }

        try {
            context?.startForegroundService(alarmForegroundIntent)
        } catch (e: Exception) {
            context?.startService(alarmForegroundIntent)
        }
    }
}
