package lamdx4.uis.ptithcm.ui.more.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lamdx4.uis.ptithcm.common.activityViewModel
import lamdx4.uis.ptithcm.data.model.CourseItem
import lamdx4.uis.ptithcm.data.model.RegisteredSubject
import lamdx4.uis.ptithcm.data.model.SubjectFilter
import lamdx4.uis.ptithcm.ui.AppViewModel
import lamdx4.uis.ptithcm.ui.theme.PTITTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseRegistrationScreen(
    modifier: Modifier = Modifier,
    registrationViewModel: CourseRegistrationViewModel = hiltViewModel(),
    appViewModel: AppViewModel = activityViewModel<AppViewModel>()
) {
    val userState by appViewModel.uiState.collectAsState()
    val registrationState by registrationViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Môn mở", "Đã đăng ký")
    val studentClass = userState.profile?.lop

    // Khởi tạo ViewModel khi studentClass thay đổi
    LaunchedEffect(studentClass) {
        registrationViewModel.initRegistration(studentClass)
    }

    val filters = registrationState.subjectFilters
    val selectedFilter = registrationState.selectedFilter

    // Show snackbar for messages
    registrationState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error snackbar
            registrationViewModel.clearMessages()
        }
    }

    registrationState.successMessage?.let { message ->
        LaunchedEffect(message) {
            // Show success snackbar
            registrationViewModel.clearMessages()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Đăng ký môn học",
                        style = PTITTypography.screenTitle
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    IconButton(
                        onClick = { registrationViewModel.refreshData() }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Làm mới",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Registration Note (giữ lại cho cả 2 tab)
            if (registrationState.registrationNote.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = registrationState.registrationNote,
                        style = PTITTypography.caption,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                style = PTITTypography.buttonText
                            )
                        }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTab) {
                0 -> AvailableSubjectsContent(
                    subjects = registrationState.filteredSubjects,
                    filters = registrationState.subjectFilters,
                    selectedFilter = registrationState.selectedFilter,
                    isLoading = registrationState.isLoading,
                    isInRegistrationTime = registrationState.isInRegistrationTime,
                    searchQuery = registrationState.searchQuery,
                    onFilterSelected = { filter ->
                        registrationViewModel.selectFilter(filter)
                    },
                    onSearchQueryChanged = { query ->
                        registrationViewModel.updateSearchQuery(query)
                    },
                    onRegisterSubject = { item ->
                        registrationViewModel.registerSubject(item.group)
                    }
                )

                1 -> RegisteredSubjectsTabContent(
                    subjects = registrationState.registeredSubjects,
                    isLoading = registrationState.isLoading,
                    onUnregisterSubject = { subject ->
                        registrationViewModel.unregisterSubject(subject)
                    }
                )
            }
        }
    }
}

@Composable
private fun RegistrationSummaryCard(
    registeredSubjects: List<RegisteredSubject>,
) {
    // Sử dụng sum creditsText (String) để lấy tổng số tín chỉ
    val totalCredits = registeredSubjects.sumOf { it.subjectGroup.creditsText.toIntOrNull() ?: 0 }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                label = "Số môn",
                value = "${registeredSubjects.size}",
                color = Color(0xFF07100B)
            )
            HorizontalDivider(
                modifier = Modifier
                    .height(32.dp)
                    .width(1.dp),
                thickness = DividerDefaults.Thickness,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            SummaryItem(
                label = "Tín chỉ",
                value = "$totalCredits",
                color = Color(0xFF07100B)
            )
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Text(
            text = value,
            style = PTITTypography.numericDisplay.copy(fontSize = PTITTypography.bodyContent.fontSize),
            color = color
        )
        Text(
            text = label,
            style = PTITTypography.caption.copy(fontWeight = FontWeight.Normal),
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectFilterDropdown(
    filters: List<SubjectFilter>,
    selectedFilter: SubjectFilter?,
    onFilterSelected: (SubjectFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedFilter?.description ?: "Chọn loại môn học",
            onValueChange = {},
            readOnly = true,
            label = { Text("Loại môn học", style = PTITTypography.caption) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            textStyle = PTITTypography.bodyContent
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filters.forEach { filter ->
                DropdownMenuItem(
                    text = {
                        Text(
                            filter.description,
                            style = PTITTypography.bodyContent
                        )
                    },
                    onClick = {
                        onFilterSelected(filter)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun AvailableSubjectsContent(
    subjects: List<CourseItem>,
    filters: List<SubjectFilter>,
    selectedFilter: SubjectFilter?,
    isLoading: Boolean,
    isInRegistrationTime: Boolean,
    searchQuery: String,
    onFilterSelected: (SubjectFilter) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onRegisterSubject: (CourseItem) -> Unit
) {
    Column {
        // Filter dropdown
        if (filters.isNotEmpty()) {
            SubjectFilterDropdown(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        // Search input for filter 10, 4, 3
        if (selectedFilter?.value == 10 || selectedFilter?.value == 4 || selectedFilter?.value == 3) {
            val label = when (selectedFilter?.value) {
                10 -> "Tìm kiếm mã hoặc tên môn học"
                4 -> "Lọc theo lớp (ds_lop)"
                3 -> "Lọc theo khoa (ds_khoa)"
                else -> "Tìm kiếm"
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                label = { Text(label, style = PTITTypography.caption) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                singleLine = true,
                textStyle = PTITTypography.bodyContent
            )
        }

        // Registration time warning
        if (!isInRegistrationTime) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Hiện tại không trong thời gian đăng ký môn học",
                        style = PTITTypography.caption,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            "Đang tải danh sách môn học...",
                            style = PTITTypography.bodyContent,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            subjects.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Không có môn học nào",
                            style = PTITTypography.bodyContent,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(subjects) { item ->
                        AvailableSubjectCard(
                            item = item,
                            canRegister = isInRegistrationTime,
                            onRegister = { onRegisterSubject(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AvailableSubjectCard(
    item: CourseItem,
    canRegister: Boolean,
    onRegister: () -> Unit
) {
    val group = item.group
    val subject = item.subject
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                !group.isEnabled -> MaterialTheme.colorScheme.surfaceVariant
                group.remaining <= 0 -> MaterialTheme.colorScheme.errorContainer
                group.isRegistered -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject?.name ?: group.subjectName,
                        style = PTITTypography.cardTitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${group.subjectCode} • Nhóm ${group.groupName}",
                        style = PTITTypography.caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Register button
                when {
                    group.isRegistered -> {
                        Badge(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                        ) {
                            Text(
                                "Đã đăng ký",
                                style = PTITTypography.badgeText,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }

                    !group.isEnabled -> {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ) {
                            Text(
                                "Không thể đăng ký",
                                style = PTITTypography.badgeText
                            )
                        }
                    }

                    group.remaining <= 0 -> {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        ) {
                            Text(
                                "Hết chỗ",
                                style = PTITTypography.badgeText,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    !canRegister -> {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ) {
                            Text(
                                "Hết hạn",
                                style = PTITTypography.badgeText
                            )
                        }
                    }

                    else -> {
                        IconButton(
                            onClick = onRegister,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Đăng ký",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Subject details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SubjectDetailChip(
                    label = "TC",
                    value = group.credit
                )
                    SubjectDetailChip(
                    label = "Lớp",
                    value = group.className
                )
                SubjectDetailChip(
                    label = "Còn lại",
                    value = "${group.remaining}/${group.capacity}",
                    color = when {
                        group.remaining <= 0 -> Color(0xFFF44336)
                        group.remaining <= 5 -> Color(0xFFFF9800)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            if (!group.teacherName.isNullOrBlank())
                Text(
                    text = "Tên GV: ${group.teacherName}",
                    style = PTITTypography.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            // Schedule info
            if (group.schedule.isNotEmpty()) {
                Text(
                    text = "Lịch: ${group.schedule}",
                    style = PTITTypography.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Special indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (group.isOverload) {
                    Badge(
                        containerColor = Color(0xFFFF9800).copy(alpha = 0.2f)
                    ) {
                        Text(
                            "Vượt",
                            style = PTITTypography.badgeText,
                            color = Color(0xFFFF9800)
                        )
                    }
                }
                if (group.isRepeat) {
                    Badge(
                        containerColor = Color(0xFF2196F3).copy(alpha = 0.2f)
                    ) {
                        Text(
                            "Học lại",
                            style = PTITTypography.badgeText,
                            color = Color(0xFF2196F3)
                        )
                    }
                }
                if (group.isCurriculumSubject) {
                    Badge(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    ) {
                        Text(
                            "CTĐT",
                            style = PTITTypography.badgeText,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun RegisteredSubjectsTabContent(
    subjects: List<RegisteredSubject>,
    isLoading: Boolean,
    onUnregisterSubject: (RegisteredSubject) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (subjects.isNotEmpty()) {
            RegistrationSummaryCard(
                registeredSubjects = subjects,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            "Đang tải môn đã đăng ký...",
                            style = PTITTypography.bodyContent,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            subjects.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Chưa đăng ký môn nào",
                            style = PTITTypography.bodyContent,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(subjects) { subject ->
                        RegisteredSubjectCard(
                            subject = subject,
                            onUnregister = { onUnregisterSubject(subject) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RegisteredSubjectCard(
    subject: RegisteredSubject,
    onUnregister: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),

            ),

        ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.subjectGroup.subjectName,
                        style = PTITTypography.cardTitle.copy(fontSize = PTITTypography.bodyContent.fontSize),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Unregister button
                if (subject.canDelete && !subject.isWithdrawn) {
                    IconButton(
                        onClick = onUnregister,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Hủy đăng ký",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Subject details
            // Không hiển thị học phí, chỉ hiển thị số tín chỉ nếu muốn
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mã môn: ${subject.subjectGroup.subjectCode} • Nhóm ${subject.subjectGroup.groupCode}",
                    style = PTITTypography.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row() {
                    Text(
                        text = "TC: ",
                        style = PTITTypography.caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = subject.subjectGroup.creditsText.toInt().toString(),
                        style = PTITTypography.caption.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }
            }

            // Registration info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đăng ký: ${subject.registeredAt}",
                    style = PTITTypography.caption.copy(fontWeight = FontWeight.Light),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Status badge
                Badge(
                    containerColor = when {
                        subject.isWithdrawn -> MaterialTheme.colorScheme.error.copy(alpha = 0.18f)
                        else -> Color(0xFF4CAF50).copy(alpha = 0.18f)
                    }
                ) {
                    Text(
                        text = if (subject.isWithdrawn) "Đã rút" else "Đã đăng ký",
                        style = PTITTypography.badgeText.copy(fontWeight = FontWeight.Medium),
                        color = if (subject.isWithdrawn) MaterialTheme.colorScheme.error else Color(
                            0xFF388E3C
                        )
                    )
                }
            }

            // Schedule info
            if (subject.subjectGroup.schedule.isNotEmpty()) {
                Text(
                    text = "Lịch: ${subject.subjectGroup.schedule}",
                    style = PTITTypography.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SubjectDetailChip(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = PTITTypography.caption.copy(fontWeight = FontWeight.Medium),
            color = color
        )
        Text(
            text = label,
            style = PTITTypography.caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
