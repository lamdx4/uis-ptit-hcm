package lamdx4.uis.ptithcm.ui.more.sync

import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material3.LinearProgressIndicator
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import lamdx4.uis.ptithcm.data.model.Semester
import lamdx4.uis.ptithcm.ui.theme.PTITTypography

import lamdx4.uis.ptithcm.data.repository.CalendarSyncRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarSyncScreen(
    onNavigateBack: () -> Unit,
    viewModel: CalendarSyncViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val semesters by viewModel.semesters.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val duplicateDialogState by viewModel.duplicateEventsDialogState.collectAsState()

    // Khởi tạo Credential Manager
    // Launcher cho Google Identity Services (Play Services Auth)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val authorizationResult = Identity.getAuthorizationClient(context)
                .getAuthorizationResultFromIntent(result.data)
            val accessToken = authorizationResult.accessToken
            if (!accessToken.isNullOrEmpty()) {
                viewModel.onGoogleAuthSuccess(accessToken)
            } else {
                viewModel.onGoogleAuthError("Không lấy được access token từ Google")
            }
        } else {
            viewModel.onGoogleAuthError("Đăng nhập Google bị huỷ hoặc lỗi")
        }
    }

    // Removed problematic LaunchedEffect that may cause navigation issues

    // Dialog xác nhận khi đã tồn tại calendar học kỳ
    if (duplicateDialogState is CalendarSyncRepository.CalendarEventCheckResult.HasEvent) {
        AlertDialog(
            onDismissRequest = { viewModel.onUserCancelDuplicateDialog() },
            title = { Text("Đã tồn tại lịch học kỳ này trên Google Calendar") },
            text = {
                Text("Bạn muốn ghi đè toàn bộ lịch học kỳ này trên Google Calendar hay chỉ thêm mới các sự kiện?")
            },
            confirmButton = {
                Button(onClick = { viewModel.onUserConfirmDeleteAndSync() }) {
                    Text("Ghi đè (xoá cũ, thêm mới)")
                }
            },
            dismissButton = {
                Row {
                    Button(onClick = { viewModel.onUserConfirmAppendAndSync() }) {
                        Text("Chỉ thêm mới")
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(onClick = { viewModel.onUserCancelDuplicateDialog() }) {
                        Text("Huỷ")
                    }
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Đồng bộ Google Calendar",
                        style = PTITTypography.screenTitle
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Đồng bộ thời khóa biểu",
                            style = PTITTypography.sectionTitle
                        )
                    }
                    Text(
                        "Tự động thêm lịch học vào Google Calendar của bạn với đầy đủ thông tin môn học, giảng viên, phòng học và nhắc nhở.",
                        style = PTITTypography.bodyContent,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Error display
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            error,
                            style = PTITTypography.errorText,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Success message
            if (uiState.lastSyncResult.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                        Text(
                            uiState.lastSyncResult,
                            style = PTITTypography.successText,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                !uiState.hasCalendarPermission -> {
                    GoogleSignInSection {
                        Log.d("CalendarSyncScreen", "Google Sign In button clicked")
                        val requestedScopes = listOf(
                            Scope("https://www.googleapis.com/auth/calendar")
                        )
                        val request = AuthorizationRequest.Builder()
                            .setRequestedScopes(requestedScopes)
                            .requestOfflineAccess("916844478141-ka7o7agkbpdaijeee55g5ebvti3ohlbr.apps.googleusercontent.com") // Thay bằng Web Client ID của bạn
                            .build()
                        Identity.getAuthorizationClient(context)
                            .authorize(request)
                            .addOnSuccessListener { result ->
                                if (result.hasResolution()) {
                                    launcher.launch(
                                        IntentSenderRequest.Builder(result.pendingIntent!!.intentSender)
                                            .build()
                                    )
                                } else {
                                    val token = result.accessToken
                                    if (!token.isNullOrEmpty()) {
                                        viewModel.onGoogleAuthSuccess(token)
                                    } else {
                                        viewModel.onGoogleAuthError("Không lấy được access token từ Google")
                                    }
                                }
                            }
                            .addOnFailureListener {
                                viewModel.onGoogleAuthError(it.message ?: "Lỗi không xác định")
                            }
                    }
                }

                uiState.isLoadingSemesters -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Đang tải danh sách học kỳ...")
                    }
                }

                semesters.isEmpty() -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Không tìm thấy học kỳ nào",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = { viewModel.loadSemesters() }) {
                            Text("Thử lại")
                        }
                    }
                }

                else -> {
                    SemesterSelectionSection(
                        semesters = semesters,
                        selectedSemester = selectedSemester,
                        onSemesterSelected = { viewModel.selectSemester(it) },
                        isSyncing = uiState.isSyncing,
                        syncProgress = uiState.syncProgress,
                        syncProgressPercent = uiState.syncProgressPercent,
                        remindMinutes = uiState.remindMinutes,
                        onRemindMinutesChanged = { viewModel.setRemindMinutes(it) },
                        onSyncClicked = { viewModel.syncSelectedSemester() }
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleSignInSection(
    onSignInClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                "Cần quyền truy cập Google Calendar",
                style = PTITTypography.sectionTitle,
                textAlign = TextAlign.Center
            )

            Text(
                "Để đồng bộ thời khóa biểu, ứng dụng cần quyền truy cập vào Google Calendar của bạn",
                style = PTITTypography.bodyContent,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onSignInClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AccountCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng nhập Google", style = PTITTypography.buttonText)
            }
        }
    }
}

@Composable
private fun SemesterSelectionSection(
    semesters: List<Semester>,
    selectedSemester: Semester?,
    onSemesterSelected: (Semester) -> Unit,
    isSyncing: Boolean,
    syncProgress: String,
    syncProgressPercent: Float,
    remindMinutes: Int,
    onRemindMinutesChanged: (Int) -> Unit,
    onSyncClicked: () -> Unit
) {
    val remindOptions = listOf(0, 5, 10, 15, 30, 60)
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Chọn học kỳ cần đồng bộ:",
            style = PTITTypography.sectionTitle,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(semesters) { semester ->
                SemesterCard(
                    semester = semester,
                    isSelected = semester == selectedSemester,
                    onSelected = { onSemesterSelected(semester) }
                )
            }
        }

        // Tuỳ chọn nhắc nhở
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text("Nhắc trước:", style = PTITTypography.bodyContent)
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(if (remindMinutes == 0) "Không nhắc" else "$remindMinutes phút")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    remindOptions.forEach { min ->
                        DropdownMenuItem(
                            text = { Text(if (min == 0) "Không nhắc" else "$min phút") },
                            onClick = {
                                onRemindMinutesChanged(min)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        // Sync progress
        if (isSyncing && syncProgress.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            syncProgress,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    LinearProgressIndicator(
                        progress = { syncProgressPercent },
                        modifier = Modifier.fillMaxWidth(),
                        color = ProgressIndicatorDefaults.linearColor,
                        trackColor = ProgressIndicatorDefaults.linearTrackColor,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    )
                }
            }
        }

        // Sync button
        Button(
            onClick = onSyncClicked,
            enabled = selectedSemester != null && !isSyncing,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isSyncing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(Icons.Default.CloudSync, contentDescription = null)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (isSyncing) "Đang đồng bộ..."
                else "Đồng bộ học kỳ ${selectedSemester?.semesterName ?: ""}"
            )
        }
    }
}

@Composable
private fun SemesterCard(
    semester: Semester,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            )
        else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = null,
                tint = if (isSelected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    semester.semesterName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "Mã học kỳ: ${semester.semesterCode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Đã chọn",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp)
                )
            }
        }
    }
}

