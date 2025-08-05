package lamdx4.uis.ptithcm.ui.more.notifications

import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import lamdx4.uis.ptithcm.ui.theme.PTITColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    notificationId: String,
    navController: NavController? = null,
    viewModel: NotificationViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Find the notification in the current list
    val notification = uiState.notifications.find { it.id == notificationId }

    LaunchedEffect(notificationId) {
        // Mark as read when screen opens
        viewModel.markNotificationAsRead(notificationId)
    }

    Surface(
        modifier = modifier
    ) {
        if (notification == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator()
                    Text(
                        "Đang tải thông báo...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title with priority indicator
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            if (notification.priority == NotificationPriority.HIGH) {
                                Icon(
                                    Icons.Default.PriorityHigh,
                                    contentDescription = "High priority",
                                    modifier = Modifier.size(20.dp),
                                    tint = PTITColors.redDefault
                                )
                            }

                            Text(
                                text = notification.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        // Metadata
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Sender
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Người gửi",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = notification.sender,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Date
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(
                                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Thời gian gửi",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = notification.sentAt,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Status badges
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (notification.mustView) {
                                Text(
                                    text = "BẮT BUỘC ĐỌC",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PTITColors.redDefault,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(
                                            PTITColors.redDefault.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Content Card with WebView
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Nội dung",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // WebView for HTML content
                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    webViewClient = object : WebViewClient() {
                                        override fun shouldOverrideUrlLoading(
                                            view: WebView?,
                                            url: String?
                                        ): Boolean {
                                            // Handle link clicks - open in external browser
                                            url?.let {
                                                try {
                                                    val intent = android.content.Intent(
                                                        android.content.Intent.ACTION_VIEW,
                                                        android.net.Uri.parse(it)
                                                    )
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // Handle error - maybe show a toast
                                                }
                                            }
                                            return true
                                        }

                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            // Adjust WebView height to content
                                            view?.evaluateJavascript(
                                                "(function() { return document.body.scrollHeight; })();"
                                            ) { height ->
                                                try {
                                                    val contentHeight = height.toFloat()
                                                    view.layoutParams = view.layoutParams.apply {
                                                        this.height =
                                                            (contentHeight * context.resources.displayMetrics.density).toInt()
                                                    }
                                                } catch (e: Exception) {
                                                    // Handle parsing error
                                                }
                                            }
                                        }
                                    }

                                    with(settings) {
                                        javaScriptEnabled = true // Enable for height calculation
                                        domStorageEnabled = false
                                        allowFileAccess = false
                                        allowContentAccess = false
                                        allowFileAccessFromFileURLs = false
                                        allowUniversalAccessFromFileURLs = false
                                        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                                        cacheMode = WebSettings.LOAD_NO_CACHE

                                        // Better text rendering
                                        textZoom = 100
                                        minimumFontSize = 14
                                        defaultFontSize = 16

                                        // Responsive design
                                        useWideViewPort = true
                                        loadWithOverviewMode = true
                                        builtInZoomControls = false
                                        displayZoomControls = false
                                    }

                                    // Set transparent background
                                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            update = { webView ->
                                val isDarkMode = false // You can get this from theme
                                val textColor = if (isDarkMode) "#FFFFFF" else "#333333"
                                val linkColor = if (isDarkMode) "#64B5F6" else "#1976D2"
                                val backgroundColor = if (isDarkMode) "#121212" else "transparent"

                                val styledHtml = """
                                    <!DOCTYPE html>
                                    <html>
                                    <head>
                                        <meta charset="UTF-8">
                                        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                                        <style>
                                            * {
                                                box-sizing: border-box;
                                            }
                                            body {
                                                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                                                font-size: 16px;
                                                line-height: 1.6;
                                                color: $textColor;
                                                margin: 0;
                                                padding: 0;
                                                background-color: $backgroundColor;
                                                word-wrap: break-word;
                                                overflow-wrap: break-word;
                                            }
                                            p {
                                                margin: 0 0 16px 0;
                                                text-align: justify;
                                            }
                                            ul, ol {
                                                margin: 16px 0;
                                                padding-left: 24px;
                                            }
                                            li {
                                                margin: 8px 0;
                                                text-align: justify;
                                            }
                                            strong, b {
                                                font-weight: 600;
                                                color: $linkColor;
                                            }
                                            a {
                                                color: $linkColor;
                                                text-decoration: none;
                                                word-break: break-all;
                                            }
                                            a:hover {
                                                text-decoration: underline;
                                            }
                                            h1, h2, h3, h4, h5, h6 {
                                                color: $linkColor;
                                                margin: 20px 0 12px 0;
                                                font-weight: 600;
                                                line-height: 1.3;
                                            }
                                            h1 { font-size: 24px; }
                                            h2 { font-size: 22px; }
                                            h3 { font-size: 20px; }
                                            h4 { font-size: 18px; }
                                            h5 { font-size: 16px; }
                                            h6 { font-size: 14px; }
                                            
                                            blockquote {
                                                border-left: 4px solid $linkColor;
                                                margin: 16px 0;
                                                padding: 8px 16px;
                                                background-color: ${if (isDarkMode) "#1E1E1E" else "#F5F5F5"};
                                                font-style: italic;
                                            }
                                            
                                            code {
                                                background-color: ${if (isDarkMode) "#2D2D2D" else "#F0F0F0"};
                                                padding: 2px 6px;
                                                border-radius: 4px;
                                                font-family: 'Courier New', monospace;
                                                font-size: 14px;
                                            }
                                            
                                            pre {
                                                background-color: ${if (isDarkMode) "#2D2D2D" else "#F0F0F0"};
                                                padding: 12px;
                                                border-radius: 8px;
                                                overflow-x: auto;
                                                margin: 16px 0;
                                            }
                                            
                                            img {
                                                max-width: 100%;
                                                height: auto;
                                                border-radius: 8px;
                                                margin: 8px 0;
                                            }
                                            
                                            table {
                                                width: 100%;
                                                border-collapse: collapse;
                                                margin: 16px 0;
                                            }
                                            
                                            th, td {
                                                border: 1px solid ${if (isDarkMode) "#444" else "#DDD"};
                                                padding: 8px 12px;
                                                text-align: left;
                                            }
                                            
                                            th {
                                                background-color: ${if (isDarkMode) "#333" else "#F9F9F9"};
                                                font-weight: 600;
                                            }
                                            
                                            /* Handle empty paragraphs */
                                            p:empty {
                                                margin: 8px 0;
                                                height: 8px;
                                            }
                                            
                                            /* Responsive adjustments */
                                            @media (max-width: 600px) {
                                                body {
                                                    font-size: 15px;
                                                }
                                                h1 { font-size: 22px; }
                                                h2 { font-size: 20px; }
                                                h3 { font-size: 18px; }
                                            }
                                        </style>
                                    </head>
                                    <body>
                                        ${notification.content}
                                    </body>
                                    </html>
                                """.trimIndent()

                                webView.loadDataWithBaseURL(
                                    null,
                                    styledHtml,
                                    "text/html",
                                    "UTF-8",
                                    null
                                )
                            }
                        )
                    }
                }

                // Read Status Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (notification.isRead) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (notification.isRead) Icons.Default.DoneAll else Icons.Default.Notifications,
                            contentDescription = null,
                            tint = if (notification.isRead) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            },
                            modifier = Modifier.size(20.dp)
                        )

                        Text(
                            text = if (notification.isRead) {
                                "Thông báo này đã được đọc"
                            } else {
                                "Thông báo này đã được đánh dấu là đã đọc"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (notification.isRead) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            }
                        )
                    }
                }

                // Add some bottom padding for better scrolling experience
                Box(modifier = Modifier.padding(bottom = 24.dp))
            }
        }
    }
}