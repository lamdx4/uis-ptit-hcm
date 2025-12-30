package lamdx4.uis.ptithcm.ui.fee

import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.glance.LocalContext
import lamdx4.uis.ptithcm.util.downloadFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportWebScreen(
    url: String,
    cookies: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tải bảng điểm/Học phí", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        // 1. Cấu hình WebView
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            builtInZoomControls = true
                            displayZoomControls = false
                            // Cho phép tải file
                            allowFileAccess = true
                        }

                        // 2. BƠM COOKIE
                        val cookieManager = CookieManager.getInstance()
                        cookieManager.setAcceptCookie(true)
                        cookieManager.setCookie(url, cookies)
                        cookieManager.flush()

                        // 3. Client xử lý hiển thị
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false // Luôn mở trong WebView này, không nhảy ra Chrome
                            }
                        }

                        // 4. BẮT SỰ KIỆN DOWNLOAD
                        setDownloadListener { downloadUrl, userAgent, contentDisposition, mimetype, contentLength ->
                            // Lấy tên file từ server trả về
                            val fileName = URLUtil.guessFileName(downloadUrl, contentDisposition, mimetype)

                            // Lấy cookie chuẩn từ WebView
                            val currentCookie = CookieManager.getInstance().getCookie(downloadUrl)

                            val result = downloadFile(
                                context = ctx,
                                url = downloadUrl,
                                fileName = fileName,
                                cookie = currentCookie,
                                userAgent = userAgent
                            )

                            if (result != -1L) {
                                // Tải xong thì có thể đóng màn hình này
                                // onBack()
                            }
                        }

                        // 5. Load trang
                        loadUrl(url)
                    }
                },
                update = { webView ->
                    // update url nếu cần
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
