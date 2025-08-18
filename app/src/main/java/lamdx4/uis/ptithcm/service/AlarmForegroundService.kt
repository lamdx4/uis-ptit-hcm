package lamdx4.uis.ptithcm.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import lamdx4.uis.ptithcm.R
import lamdx4.uis.ptithcm.ui.exam.RingingAlarmActivity

class AlarmForegroundService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        val channelId = "alarm_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val channel = NotificationChannel(
                channelId,
                "Alarm Notification",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, intent?.getIntExtra("requestCode", 0) ?: -1, Intent(
                this,
                RingingAlarmActivity::class.java
            ),
            PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = PendingIntent.getBroadcast(
            this,
            intent?.getIntExtra("requestCode", 0) ?: -1,
            Intent(this, AlarmReceiver::class.java).apply {
                action = "alarm.ACTION_DISMISS"
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = PendingIntent.getBroadcast(
            this,
            intent?.getIntExtra("requestCode", 0) ?: -1,
            Intent(this, AlarmReceiver::class.java).apply {
                action = "alarm.ACTION_SNOOZE"
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Báo thức")
            .setContentText("Đang báo thức")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(pendingIntent, true)
            .addAction(0, "Hủy", cancelIntent)
            .addAction(0, "Nhắc lại", snoozeIntent)
            .build()

        startForeground(1, notification)

        // Play ringtone if available

        return START_NOT_STICKY
    }
}
