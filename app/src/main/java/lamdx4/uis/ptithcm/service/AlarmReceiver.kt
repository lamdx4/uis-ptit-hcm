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
                        val alarm = alarms.find { it.label == label }
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
            context?.stopService(Intent(context, AlarmForegroundService::class.java))
            if (requestCode != -1) {
                val pendingResult = goAsync()
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val db = AlarmDatabase.getInstance(context!!)
                        val alarms = db.alarmDao().getAllAlarms()
                        val alarm = alarms.find { it.label == label }
                        alarm?.time += 300_000
                        if (alarm != null) {
                            db.alarmDao().updateAlarm(alarm)
                            AlarmScheduler().scheduleExactAlarm(
                                context = context,
                                requestCode = alarm.time.toInt(),
                                triggerAtMillis = alarm.time,
                                label = alarm.label
                            )
                        }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
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
