package lamdx4.uis.ptithcm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "alarm.ACTION_DISMISS") {
            context?.stopService(Intent(context, AlarmForegroundService::class.java))
            return
        } else if (intent?.action == "alarm.ACTION_SNOOZE") {
            return
        }

        val aRIntent = Intent(context, AlarmForegroundService::class.java).apply {
            putExtra("requestCode", intent?.getIntExtra("requestCode", 0))
        }

        try {
            context?.startForegroundService(aRIntent)
        } catch (e: Exception) {
            context?.startService(aRIntent)
        }
    }
}
