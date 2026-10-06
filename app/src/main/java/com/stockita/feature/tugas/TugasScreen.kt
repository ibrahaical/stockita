package com.stockita.feature.tugas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stockita.core.database.entity.TaskEntity
import com.stockita.feature.tugas.model.TaskDateFormatter
import com.stockita.feature.tugas.model.TaskPriority
import com.stockita.feature.tugas.model.TaskStatus
import com.stockita.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TugasScreen(
    viewModel: TugasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Orange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Tugas", modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFAFAFB))
        ) {
            // 1. Header Ringkasan & Progress
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daftar Tugas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )
                        Text(
                            text = "${uiState.doneCount} dari ${uiState.totalCount} selesai",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSoft,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = {
                            if (uiState.totalCount > 0) uiState.doneCount.toFloat() / uiState.totalCount else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Orange,
                        trackColor = OrangeSoft,
                    )
                }
            }

            // 2. Search Bar
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari tugas, restock, atau catatan...", color = InkSoft, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = InkSoft)
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Hapus", tint = InkSoft)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Orange,
                        unfocusedBorderColor = Line
                    )
                )
            }

            // 3. Status Tabs (Semua, To Do, In Progress, Selesai)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tab "Semua"
                StatusTabChip(
                    label = "Semua",
                    count = uiState.totalCount,
                    isSelected = uiState.selectedStatusTab == null,
                    onClick = { viewModel.setStatusTab(null) }
                )

                // Tab "To Do"
                StatusTabChip(
                    label = "To Do",
                    count = uiState.todoCount,
                    isSelected = uiState.selectedStatusTab == TaskStatus.TODO,
                    onClick = { viewModel.setStatusTab(TaskStatus.TODO) }
                )

                // Tab "In Progress"
                StatusTabChip(
                    label = "In Progress",
                    count = uiState.inProgressCount,
                    isSelected = uiState.selectedStatusTab == TaskStatus.IN_PROGRESS,
                    onClick = { viewModel.setStatusTab(TaskStatus.IN_PROGRESS) }
                )

                // Tab "Selesai"
                StatusTabChip(
                    label = "Selesai",
                    count = uiState.doneCount,
                    isSelected = uiState.selectedStatusTab == TaskStatus.DONE,
                    onClick = { viewModel.setStatusTab(TaskStatus.DONE) }
                )
            }

            // 4. Priority Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Prioritas:", style = MaterialTheme.typography.labelMedium, color = InkSoft)

                // All Priority
                FilterChip(
                    selected = uiState.selectedPriorityFilter == null,
                    onClick = { viewModel.setPriorityFilter(null) },
                    label = { Text("Semua", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangeSoft,
                        selectedLabelColor = OrangeDeep,
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, if (uiState.selectedPriorityFilter == null) Orange else Line),
                    shape = RoundedCornerShape(12.dp)
                )

                // High Priority
                FilterChip(
                    selected = uiState.selectedPriorityFilter == TaskPriority.HIGH,
                    onClick = {
                        viewModel.setPriorityFilter(if (uiState.selectedPriorityFilter == TaskPriority.HIGH) null else TaskPriority.HIGH)
                    },
                    label = { Text("🔴 Tinggi", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFEE2E2),
                        selectedLabelColor = Color(0xFFDC2626),
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, if (uiState.selectedPriorityFilter == TaskPriority.HIGH) Color(0xFFDC2626) else Line),
                    shape = RoundedCornerShape(12.dp)
                )

                // Medium Priority
                FilterChip(
                    selected = uiState.selectedPriorityFilter == TaskPriority.MEDIUM,
                    onClick = {
                        viewModel.setPriorityFilter(if (uiState.selectedPriorityFilter == TaskPriority.MEDIUM) null else TaskPriority.MEDIUM)
                    },
                    label = { Text("🟡 Sedang", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFEF3C7),
                        selectedLabelColor = Color(0xFFD97706),
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, if (uiState.selectedPriorityFilter == TaskPriority.MEDIUM) Color(0xFFD97706) else Line),
                    shape = RoundedCornerShape(12.dp)
                )

                // Low Priority
                FilterChip(
                    selected = uiState.selectedPriorityFilter == TaskPriority.LOW,
                    onClick = {
                        viewModel.setPriorityFilter(if (uiState.selectedPriorityFilter == TaskPriority.LOW) null else TaskPriority.LOW)
                    },
                    label = { Text("⚪ Rendah", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFF3F4F6),
                        selectedLabelColor = Color(0xFF4B5563),
                        containerColor = Color.White
                    ),
                    border = BorderStroke(1.dp, if (uiState.selectedPriorityFilter == TaskPriority.LOW) Color(0xFF4B5563) else Line),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 5. List of Task Cards
            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Orange)
                } else if (uiState.error != null) {
                    Text(text = uiState.error!!, color = Color(0xFFDC2626), modifier = Modifier.align(Alignment.Center))
                } else if (uiState.tasks.isEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFFD1D5DB),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotBlank() || uiState.selectedStatusTab != null || uiState.selectedPriorityFilter != null)
                                "Tidak ada tugas yang sesuai filter"
                            else
                                "Belum ada tugas. Tekan + untuk menambah!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkSoft
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.tasks, key = { it.id }) { task ->
                            TaskCard(
                                task = task,
                                onCycleStatus = { viewModel.cycleTaskStatus(task) },
                                onToggleDone = { checked -> viewModel.toggleTaskDone(task, checked) },
                                onEdit = { taskToEdit = task },
                                onDelete = { taskToDelete = task }
                            )
                        }
                    }
                }
            }
        }

        // Dialog Tambah Tugas
        if (showAddDialog) {
            TaskFormDialog(
                title = "Tambah Tugas",
                initialTask = null,
                onDismiss = { showAddDialog = false },
                onSave = { title, note, priority, status, dueAt ->
                    viewModel.addTask(title, note, priority, status, dueAt)
                    showAddDialog = false
                }
            )
        }

        // Dialog Edit Tugas
        taskToEdit?.let { task ->
            TaskFormDialog(
                title = "Edit Tugas",
                initialTask = task,
                onDismiss = { taskToEdit = null },
                onSave = { title, note, priority, status, dueAt ->
                    viewModel.updateTask(
                        task.copy(
                            title = title,
                            note = note,
                            priority = priority,
                            status = status,
                            isDone = (status == TaskStatus.DONE.code),
                            dueAt = dueAt
                        )
                    )
                    taskToEdit = null
                }
            )
        }

        // Dialog Konfirmasi Hapus
        taskToDelete?.let { task ->
            AlertDialog(
                onDismissRequest = { taskToDelete = null },
                title = { Text("Hapus Tugas", fontWeight = FontWeight.Bold) },
                text = { Text("Apakah Anda yakin ingin menghapus tugas \"${task.title}\"?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteTask(task)
                            taskToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text("Hapus", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { taskToDelete = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
fun StatusTabChip(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (isSelected) Orange else Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSelected) Orange else Line),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Ink
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                color = if (isSelected) Color.White.copy(alpha = 0.25f) else Color(0xFFF3F4F6),
                shape = CircleShape
            ) {
                Text(
                    text = "$count",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else InkSoft,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskEntity,
    onCycleStatus: () -> Unit,
    onToggleDone: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val status = if (task.isDone) TaskStatus.DONE else TaskStatus.fromCode(task.status)
    val priority = TaskPriority.fromLevel(task.priority)
    val dueDateStr = TaskDateFormatter.formatDueDate(task.dueAt)
    val isOverdue = TaskDateFormatter.isOverdue(task.dueAt) && status != TaskStatus.DONE

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(0.5.dp, Line)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Quick Status Checkbox
            Checkbox(
                checked = task.isDone,
                onCheckedChange = onToggleDone,
                colors = CheckboxDefaults.colors(
                    checkedColor = Orange,
                    uncheckedColor = InkSoft
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Main Info
            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                    color = if (task.isDone) InkSoft else Ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Optional Note
                if (!task.note.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = task.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = InkSoft,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Badges Row: Status Pill, Priority Pill, Due Date Pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Status Badge (Clickable to cycle!)
                    Surface(
                        modifier = Modifier.clickable { onCycleStatus() },
                        shape = RoundedCornerShape(8.dp),
                        color = when (status) {
                            TaskStatus.TODO -> Color(0xFFF3F4F6)
                            TaskStatus.IN_PROGRESS -> OrangeSoft
                            TaskStatus.DONE -> Color(0xFFDCFCE7)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = status.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (status) {
                                    TaskStatus.TODO -> Color(0xFF4B5563)
                                    TaskStatus.IN_PROGRESS -> OrangeDeep
                                    TaskStatus.DONE -> Color(0xFF16A34A)
                                }
                            )
                        }
                    }

                    // Priority Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = priority.badgeBg
                    ) {
                        Text(
                            text = priority.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = priority.color,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    // Due Date Badge
                    if (dueDateStr != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isOverdue) Color(0xFFFEE2E2) else Color(0xFFF3F4F6)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isOverdue) Icons.Default.Warning else Icons.Default.DateRange,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = if (isOverdue) Color(0xFFDC2626) else InkSoft
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isOverdue) "Terlewat: $dueDateStr" else dueDateStr,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isOverdue) Color(0xFFDC2626) else Ink
                                )
                            }
                        }
                    }
                }
            }

            // Actions: Edit and Delete
            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = InkSoft, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Hapus", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun TaskFormDialog(
    title: String,
    initialTask: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, note: String?, priority: Int, status: String, dueAt: Long?) -> Unit
) {
    var taskTitle by remember { mutableStateOf(initialTask?.title ?: "") }
    var taskNote by remember { mutableStateOf(initialTask?.note ?: "") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.fromLevel(initialTask?.priority ?: 1)) }
    var selectedStatus by remember { mutableStateOf(TaskStatus.fromCode(initialTask?.status)) }
    var selectedDueAt by remember { mutableStateOf<Long?>(initialTask?.dueAt) }

    val quickDates = remember {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 3600_000L
        listOf(
            "Tanpa Tenggat" to null,
            "Hari Ini" to now,
            "Besok" to (now + oneDay),
            "3 Hari" to (now + 3 * oneDay),
            "1 Minggu" to (now + 7 * oneDay)
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold, color = Ink) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Judul
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Judul Tugas *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Catatan
                OutlinedTextField(
                    value = taskNote,
                    onValueChange = { taskNote = it },
                    label = { Text("Catatan (Opsional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Status Selector
                Text("Status:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Ink)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskStatus.entries.forEach { st ->
                        val isSel = st == selectedStatus
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedStatus = st },
                            label = { Text(st.label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Orange,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Priority Selector
                Text("Prioritas:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Ink)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TaskPriority.entries.forEach { pr ->
                        val isSel = pr == selectedPriority
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedPriority = pr },
                            label = { Text(pr.label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = pr.badgeBg,
                                selectedLabelColor = pr.color
                            ),
                            border = BorderStroke(1.dp, if (isSel) pr.color else Line),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Due Date Selector
                Text("Tenggat Waktu:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Ink)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickDates.forEach { (label, ts) ->
                        val isSel = if (ts == null) selectedDueAt == null else (selectedDueAt != null && TaskDateFormatter.formatDueDate(selectedDueAt) == TaskDateFormatter.formatDueDate(ts))
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedDueAt = ts },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangeSoft,
                                selectedLabelColor = OrangeDeep
                            ),
                            border = BorderStroke(1.dp, if (isSel) Orange else Line),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        taskTitle.trim(),
                        taskNote.trim().ifBlank { null },
                        selectedPriority.level,
                        selectedStatus.code,
                        selectedDueAt
                    )
                },
                enabled = taskTitle.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Simpan", color = Color.White)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
