package lamdx4.uis.ptithcm.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import lamdx4.uis.ptithcm.data.database.AlarmDatabase

class AlarmReceiver : BroadcastReceiver() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
        val label = intent?.getStringExtra("label")
        val requestCode = intent?.getIntExtra("requestCode", -1) ?: -1

        if (intent?.action == "alarm.ACTION_DISMISS") {
            context?.stopService(Intent(context, AlarmForegroundService::class.java))
            if (requestCode != -1) {
                val pendingResult = goAsync()
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val db = AlarmDatabase.getInstance(context!!)
                        val alarms = db.alarmDao().getAllAlarms()
                        val alarm = alarms.find { it.time.toInt() == requestCode }
                        if (alarm != null) {
                            db.alarmDao().deleteAlarm(alarm)
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
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
