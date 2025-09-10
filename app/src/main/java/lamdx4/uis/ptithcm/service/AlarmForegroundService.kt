package lamdx4.uis.ptithcm.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import lamdx4.uis.ptithcm.R
import lamdx4.uis.ptithcm.ui.exam.RingingAlarmActivity

class AlarmForegroundService : Service() {
    override fun onBind(p0: Intent?): IBinder? = null

    @SuppressLint("FullScreenIntentPolicy")  // Checked permission when user click turn on alarm (ExamComponents.kt)
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        val label = intent?.getStringExtra("label") ?: "Báo thức"
        val requestCode = intent?.getIntExtra("requestCode", -1) ?: -1

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

        val ringingIntent = Intent(this, RingingAlarmActivity::class.java).apply {
            putExtra("label", label)
            putExtra("requestCode", requestCode)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, requestCode, ringingIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            Intent(this, AlarmReceiver::class.java).apply {
                action = "alarm.ACTION_DISMISS"
                putExtra("requestCode", requestCode)
                putExtra("label", label)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val snoozeIntent = PendingIntent.getBroadcast(
            this,
            requestCode,
            Intent(this, AlarmReceiver::class.java).apply {
                action = "alarm.ACTION_SNOOZE"
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Báo thức nhắc nhở")
            .setContentText(label)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(pendingIntent, true)
            .addAction(0, "Hủy", cancelIntent)
            .addAction(0, "Nhắc lại", snoozeIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(1, notification)
        }

        // Play ringtone if available

        return START_NOT_STICKY
    }
}
