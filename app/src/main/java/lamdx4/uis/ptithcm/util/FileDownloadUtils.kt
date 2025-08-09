package lamdx4.uis.ptithcm.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

// Giữ receiver ở đây để tránh bị GC
private val activeReceivers = mutableSetOf<BroadcastReceiver>()

fun downloadFile(context: Context, url: String, fileName: String) {
    val appContext = context.applicationContext

    val request = DownloadManager.Request(url.toUri())
        .setTitle(fileName)
        .setDescription("Đang tải $fileName")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

    val downloadManager = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val downloadId = downloadManager.enqueue(request)

    val onComplete = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                Toast.makeText(appContext, "$fileName đã tải xong!", Toast.LENGTH_LONG).show()
                try {
                    appContext.unregisterReceiver(this)
                } catch (_: Exception) {
                }
                activeReceivers.remove(this) // Xóa khỏi set để tránh leak
            }
        }
    }

    activeReceivers.add(onComplete)

    ContextCompat.registerReceiver(
        appContext,
        onComplete,
        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
        ContextCompat.RECEIVER_NOT_EXPORTED
    )
}
