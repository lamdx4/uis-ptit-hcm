package lamdx4.uis.ptithcm.ui.more.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import lamdx4.uis.ptithcm.data.model.RegisterGroup
import lamdx4.uis.ptithcm.data.model.RegisteredSubject
import lamdx4.uis.ptithcm.data.model.SubjectFilter
import lamdx4.uis.ptithcm.ui.theme.PTITTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseRegistrationScreen(
    modifier: Modifier = Modifier,
    registrationViewModel: CourseRegistrationViewModel = hiltViewModel()
) {
    val registrationState by registrationViewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Môn mở", "Đã đăng ký")

    // Load data when screen is first shown
    LaunchedEffect(Unit) {
        registrationViewModel.loadRegisteredSubjects()
    }

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
            // Registration Summary Card
            if (registrationState.registeredSubjects.isNotEmpty()) {
                RegistrationSummaryCard(
                    registeredSubjects = registrationState.registeredSubjects,
                    minimumCredits = registrationState.minimumCredits
                )
            }

            // Registration Note
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
                    onFilterSelected = { filter ->
                        registrationViewModel.selectFilter(filter)
                    },
                    onRegisterSubject = { subject ->
                        registrationViewModel.registerSubject(subject)
                    }
                )
                1 -> RegisteredSubjectsContent(
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
    minimumCredits: Int
) {
    val totalCredits = registeredSubjects.sumOf { it.subjectGroup.creditsNumber }.toInt()
    val totalFee = registeredSubjects.sumOf { it.estimatedFee }.toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                label = "Đã đăng ký",
                value = "${registeredSubjects.size} môn",
                color = MaterialTheme.colorScheme.primary
            )
            SummaryItem(
                label = "Tín chỉ",
                value = "$totalCredits/$minimumCredits",
                color = if (totalCredits >= minimumCredits)
                    Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
            SummaryItem(
                label = "Học phí",
                value = "${totalFee / 1000}K",
                color = MaterialTheme.colorScheme.tertiary
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
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = PTITTypography.numericDisplay,
            color = color
        )
        Text(
            text = label,
            style = PTITTypography.caption,
            color = MaterialTheme.colorScheme.onPrimaryContainer
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
    subjects: List<RegisterGroup>,
    filters: List<SubjectFilter>,
    selectedFilter: SubjectFilter?,
    isLoading: Boolean,
    isInRegistrationTime: Boolean,
    onFilterSelected: (SubjectFilter) -> Unit,
    onRegisterSubject: (RegisterGroup) -> Unit
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
                    items(subjects) { subject ->
                        AvailableSubjectCard(
                            subject = subject,
                            canRegister = isInRegistrationTime,
                            onRegister = { onRegisterSubject(subject) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AvailableSubjectCard(
    subject: RegisterGroup,
    canRegister: Boolean,
    onRegister: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                !subject.isEnabled -> MaterialTheme.colorScheme.surfaceVariant
                subject.remaining <= 0 -> MaterialTheme.colorScheme.errorContainer
                subject.isRegistered -> MaterialTheme.colorScheme.primaryContainer
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
                        text = subject.subjectName,
                        style = PTITTypography.cardTitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${subject.subjectCode} • Nhóm ${subject.groupName}",
                        style = PTITTypography.caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Register button
                when {
                    subject.isRegistered -> {
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
                    !subject.isEnabled -> {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ) {
                            Text(
                                "Không thể đăng ký",
                                style = PTITTypography.badgeText
                            )
                        }
                    }
                    subject.remaining <= 0 -> {
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
                    value = subject.credit
                )
                SubjectDetailChip(
                    label = "Lớp",
                    value = subject.className
                )
                SubjectDetailChip(
                    label = "Còn lại",
                    value = "${subject.remaining}/${subject.capacity}",
                    color = when {
                        subject.remaining <= 0 -> Color(0xFFF44336)
                        subject.remaining <= 5 -> Color(0xFFFF9800)
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            // Schedule info
            if (subject.schedule.isNotEmpty()) {
                Text(
                    text = "Lịch: ${subject.schedule}",
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
                if (subject.isOverload) {
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
                if (subject.isRepeat) {
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
                if (subject.isCurriculumSubject) {
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
private fun RegisteredSubjectsContent(
    subjects: List<RegisteredSubject>,
    isLoading: Boolean,
    onUnregisterSubject: (RegisteredSubject) -> Unit
) {
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
                contentPadding = PaddingValues(16.dp),
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

@Composable
private fun RegisteredSubjectCard(
    subject: RegisteredSubject,
    onUnregister: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                subject.isWithdrawn -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.primaryContainer
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
                        text = subject.subjectGroup.subjectName,
                        style = PTITTypography.cardTitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${subject.subjectGroup.subjectCode} • Nhóm ${subject.subjectGroup.groupCode}",
                        style = PTITTypography.caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Unregister button
                if (subject.canDelete && !subject.isWithdrawn) {
                    IconButton(
                        onClick = onUnregister,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Hủy đăng ký",
                            tint = MaterialTheme.colorScheme.error
                        )
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
                    value = subject.subjectGroup.creditsText
                )
                SubjectDetailChip(
                    label = "Lớp",
                    value = subject.subjectGroup.classCode
                )
                SubjectDetailChip(
                    label = "Học phí",
                    value = "${(subject.estimatedFee / 1000).toInt()}K"
                )
            }

            // Registration info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đăng ký: ${subject.registeredAt}",
                    style = PTITTypography.caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Status badge
                Badge(
                    containerColor = when {
                        subject.isWithdrawn -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        else -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = if (subject.isWithdrawn) "Đã rút" else "Đã đăng ký",
                        style = PTITTypography.badgeText,
                        color = if (subject.isWithdrawn)
                            MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
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
