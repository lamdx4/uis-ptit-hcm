package lamdx4.uis.ptithcm.ui.fee

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import lamdx4.uis.ptithcm.util.downloadFile
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportWebScreen(
    url: String,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Cổng thông tin Đào tạo",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            builtInZoomControls = true
                            displayZoomControls = false
                            allowFileAccess = true
                        }

                        addJavascriptInterface(BlobDownloader(ctx), "Android")

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                url: String?,
                                favicon: Bitmap?
                            ) {
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                            }
                        }

                        // Xử lý download
                        setDownloadListener { downloadUrl, userAgent, contentDisposition, mimetype, _ ->
                            if (downloadUrl.startsWith("blob:")) {
                                val script = """
                                    javascript: (function() {
                                        var xhr = new XMLHttpRequest();
                                        xhr.open('GET', '$downloadUrl', true);
                                        xhr.responseType = 'blob';
                                        xhr.onload = function(e) {
                                            if (this.status == 200) {
                                                var blob = this.response;
                                                var reader = new FileReader();
                                                reader.readAsDataURL(blob);
                                                reader.onloadend = function() {
                                                    base64data = reader.result;
                                                    // Gọi về hàm Kotlin chúng ta vừa viết
                                                    Android.getBase64FromBlobData(base64data, '$mimetype');
                                                }
                                            }
                                        };
                                        xhr.send();
                                    })();
                                """.trimIndent()

                                // Chạy đoạn script này trên WebView
                                evaluateJavascript(script, null)
                                Toast.makeText(ctx, "Đang xử lý file...", Toast.LENGTH_SHORT).show()
                            } else {
                                val cookie = CookieManager.getInstance().getCookie(downloadUrl)
                                val fileName =
                                    URLUtil.guessFileName(downloadUrl, contentDisposition, mimetype)

                                downloadFile(
                                    context = ctx,
                                    url = downloadUrl,
                                    fileName = fileName,
                                    cookie = cookie,
                                    userAgent = userAgent
                                )
                            }
                        }

                        loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

class BlobDownloader(private val context: Context) {

    @JavascriptInterface
    fun getBase64FromBlobData(base64Data: String, mimeType: String) {
        val cleanBase64 = base64Data.replaceFirst("^data:.*?,".toRegex(), "")
        try {
            val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

            // Tạo tên file
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "HocPhi_PTIT_$timeStamp.xlsx"

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                // Android 10 trở lên: Dùng MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(decodedBytes)
                    }
                    notifySuccess(fileName)
                }
            } else {
                // Android 9 trở xuống: Lưu thẳng vào thư mục Download
                val downloadDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(downloadDir, fileName)
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(decodedBytes)
                }
                notifySuccess(fileName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Lỗi lưu file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun notifySuccess(fileName: String) {
        // Cần chạy Toast trên UI Thread vì @JavascriptInterface chạy ở background thread
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(context, "Đã tải xong: $fileName", Toast.LENGTH_LONG).show()
        }
    }
}
