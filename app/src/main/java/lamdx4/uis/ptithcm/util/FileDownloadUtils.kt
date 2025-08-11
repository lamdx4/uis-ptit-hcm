package lamdx4.uis.ptithcm.util

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

// Giữ receiver để tránh bị GC
private val activeReceivers = mutableSetOf<BroadcastReceiver>()

@SuppressLint("UnspecifiedRegisterReceiverFlag")
fun downloadFile(context: Context, url: String, fileName: String): Long {
    val appContext = context.applicationContext
    val dm = appContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val safeName = sanitizeFileName(fileName)

    // Prepare request
    val request = DownloadManager.Request(url.toUri())
        .setTitle(safeName)
        .setDescription("Đang tải $safeName")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, safeName)

    // Enqueue TRƯỚC khi đăng ký receiver

    Toast.makeText(context, "Đang tải file $safeName", Toast.LENGTH_SHORT).show()

    val downloadId = try {
        dm.enqueue(request)
    } catch (e: Exception) {
        Toast.makeText(appContext, "Không thể bắt đầu tải: ${e.message}", Toast.LENGTH_LONG).show()
        return -1L
    }

    val onComplete = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            try {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L) ?: -1L

                // query status
                val cursor = dm.query(DownloadManager.Query().setFilterById(id))
                cursor?.use {
                    if (it.moveToFirst()) {
                        val status =
                            it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))

                        when (status) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                Toast.makeText(
                                    appContext,
                                    "$safeName tải thành công",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            DownloadManager.STATUS_FAILED -> {
                                val reason = try {
                                    it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON))
                                } catch (_: Exception) {
                                    -1
                                }
                                Toast.makeText(
                                    appContext,
                                    "$safeName tải thất bại (reason=$reason)",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("DownloadUtil", "Error processing download complete", e)
                Toast.makeText(appContext, "Lỗi xử lý download: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            } finally {
                // cleanup
                try {
                    appContext.unregisterReceiver(this)
                } catch (_: Exception) {
                    Log.w("DownloadUtil", "Failed to unregister receiver")
                }
                activeReceivers.remove(this)
            }
        }
    }

    // Đăng ký receiver SAU khi đã có downloadId
    try {
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)

        // Thử cách đăng ký khác nhau tùy theo API level
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(
                appContext,
                onComplete,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        } else {
            // Với API cũ hơn, dùng cách cũ
            appContext.registerReceiver(onComplete, filter)
        }

        activeReceivers.add(onComplete)
    } catch (e: Exception) {
        Log.e("DownloadUtil", "Failed to register receiver", e)
        Toast.makeText(appContext, "Không thể đăng ký receiver: ${e.message}", Toast.LENGTH_LONG)
            .show()
        return -1L
    }

    return downloadId
}

private fun sanitizeFileName(fileName: String): String {
    val sanitized = fileName
        .replace(Regex("[/\\\\]"), "_")
        .replace(Regex("\\.\\."), "_")
        .replace(Regex("[^A-Za-z0-9._-]"), "_")
    return sanitized.ifBlank { "downloaded_file" }
}