package com.stockita.feature.tugas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stockita.core.database.entity.TaskEntity
import com.stockita.feature.tugas.model.TaskCategory
import com.stockita.feature.tugas.model.TaskDateFormatter
import com.stockita.feature.tugas.model.TaskPriority
import com.stockita.feature.tugas.model.TaskStatus
import com.stockita.ui.theme.*
import java.util.Calendar

private val InkSecondary = InkSoft
private val Gray100 = Color(0xFFF3F4F6)
private val Gray200 = Line
private val Gray300 = Color(0xFFD1D5DB)
private val Gray400 = Color(0xFF9CA3AF)
private val Gray500 = InkSoft

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TugasScreen(
    viewModel: TugasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var taskToDetail by remember { mutableStateOf<TaskEntity?>(null) }

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
            // 1. Header Ringkasan & Progression Bar
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
                            text = "Tugas & To-Do",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )
                        Text(
                            text = "${uiState.completedCount} dari ${uiState.totalCount} selesai • ${uiState.progressPercentage}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSoft,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Linear Progression Indicator
                    LinearProgressIndicator(
                        progress = {
                            if (uiState.totalCount > 0) uiState.completedCount.toFloat() / uiState.totalCount else 0f
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

            Spacer(modifier = Modifier.height(12.dp))

            // 2. 4 Cards Filter Grid (2 baris x 2 kolom): Today, Scheduled, All, Overdue
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TaskCategoryCard(
                        category = TaskCategory.TODAY,
                        count = uiState.todayCount,
                        isSelected = uiState.selectedCategory == TaskCategory.TODAY,
                        icon = Icons.Default.Today,
                        onClick = { viewModel.selectCategory(TaskCategory.TODAY) },
                        modifier = Modifier.weight(1f)
                    )
                    TaskCategoryCard(
                        category = TaskCategory.SCHEDULED,
                        count = uiState.scheduledCount,
                        isSelected = uiState.selectedCategory == TaskCategory.SCHEDULED,
                        icon = Icons.Default.Schedule,
                        onClick = { viewModel.selectCategory(TaskCategory.SCHEDULED) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TaskCategoryCard(
                        category = TaskCategory.ALL,
                        count = uiState.allCount,
                        isSelected = uiState.selectedCategory == TaskCategory.ALL,
                        icon = Icons.Default.FormatListBulleted,
                        onClick = { viewModel.selectCategory(TaskCategory.ALL) },
                        modifier = Modifier.weight(1f)
                    )
                    TaskCategoryCard(
                        category = TaskCategory.OVERDUE,
                        count = uiState.overdueCount,
                        isSelected = uiState.selectedCategory == TaskCategory.OVERDUE,
                        icon = Icons.Default.Warning,
                        onClick = { viewModel.selectCategory(TaskCategory.OVERDUE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Search Input (Placeholder ringkas "Search...")
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = {
                    Text("Search...", fontSize = 14.sp, color = InkSoft)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = InkSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = InkSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Orange,
                    unfocusedBorderColor = Gray200
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Sub-title dinamis sesuai kartu yang dipencet
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = uiState.selectedCategory.subtitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = OrangeSoft
                ) {
                    Text(
                        text = "${uiState.tasks.size} tugas",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Orange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 5. List To-Do Items
            if (uiState.tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Orange.copy(alpha = 0.5f),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) "Tidak ada tugas cocok" else "Tidak ada tugas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (uiState.searchQuery.isNotBlank()) "Coba kata kunci pencarian yang lain."
                            else "Tekan tombol + untuk menambahkan tugas baru.",
                            style = MaterialTheme.typography.bodySmall,
                            color = InkSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskItemCard(
                            task = task,
                            onCardClick = { taskToDetail = task },
                            onToggleDone = { viewModel.toggleTaskDone(task, it) },
                            onEditClick = { taskToEdit = task },
                            onDeleteClick = { taskToDelete = task }
                        )
                    }
                }
            }
        }
    }

    // Modal Dialog Detail Tugas
    taskToDetail?.let { task ->
        TaskDetailDialog(
            task = task,
            onDismiss = { taskToDetail = null },
            onCycleStatus = {
                viewModel.cycleTaskStatus(task)
                taskToDetail = null
            },
            onEdit = {
                taskToDetail = null
                taskToEdit = task
            },
            onDelete = {
                taskToDetail = null
                taskToDelete = task
            }
        )
    }

    // Modal Dialog Tambah Tugas Baru
    if (showAddDialog) {
        TaskFormDialog(
            title = "Tambah Tugas Baru",
            initialTask = null,
            onDismiss = { showAddDialog = false },
            onSave = { title, note, subTasks, priority, status, dueAt ->
                viewModel.addTask(
                    title = title,
                    note = note,
                    subTasks = subTasks,
                    priority = priority,
                    status = status,
                    dueAt = dueAt
                )
                showAddDialog = false
            }
        )
    }

    // Modal Dialog Edit Tugas
    taskToEdit?.let { task ->
        TaskFormDialog(
            title = "Edit Tugas",
            initialTask = task,
            onDismiss = { taskToEdit = null },
            onSave = { title, note, subTasks, priority, status, dueAt ->
                val isDone = (status == "DONE")
                viewModel.updateTask(
                    task.copy(
                        title = title,
                        note = note,
                        subTasks = subTasks,
                        priority = priority,
                        status = status,
                        dueAt = dueAt,
                        isDone = isDone
                    )
                )
                taskToEdit = null
            }
        )
    }

    // Modal Konfirmasi Hapus Tugas
    taskToDelete?.let { task ->
        DeleteConfirmationDialog(
            taskTitle = task.title,
            onDismiss = { taskToDelete = null },
            onConfirm = {
                viewModel.deleteTask(task)
                taskToDelete = null
            }
        )
    }
}

/**
 * 2x2 Grid Card untuk Today, Scheduled, All, dan Overdue
 */
@Composable
private fun TaskCategoryCard(
    category: TaskCategory,
    count: Int,
    isSelected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBorderColor = Orange
    val activeBgColor = OrangeSoft
    val inactiveBgColor = Color.White
    val inactiveBorderColor = Gray200

    Surface(
        onClick = onClick,
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) activeBgColor else inactiveBgColor,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) activeBorderColor else inactiveBorderColor),
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(if (isSelected) Orange else Color(0xFFF3F4F6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = category.label,
                        tint = if (isSelected) Color.White else InkSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = count.toString(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Orange else Ink
                )
            }

            Text(
                text = category.label,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Orange else Ink
            )
        }
    }
}

/**
 * Kartu Utama Daftar To-Do
 * Spesifikasi UX:
 * - Kiri: Checkbox
 * - Atas: DateTime kecil (Today, 4:50 PM)
 * - Tengah: Title
 * - Bawah: Poin-poin sub-task
 * - Kanan: Action edit dan hapus
 * - Status & Priority TIDAK DITAMPILKAN di sini (hanya di Detail View).
 */
@Composable
private fun TaskItemCard(
    task: TaskEntity,
    onCardClick: () -> Unit,
    onToggleDone: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val formattedDateTime = TaskDateFormatter.formatCardDateTime(task.dueAt)
    val isOverdue = TaskDateFormatter.isOverdue(task.dueAt, task.isDone)
    val subTasks = task.subTaskList

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Gray200),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox di sisi kiri
            Checkbox(
                checked = task.isDone,
                onCheckedChange = onToggleDone,
                colors = CheckboxDefaults.colors(
                    checkedColor = Orange,
                    checkmarkColor = Color.White
                ),
                modifier = Modifier.offset(x = (-4).dp, y = (-2).dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Konten Tengah
            Column(modifier = Modifier.weight(1f)) {
                // Tanggal & Waktu kecil di atas (misal Today, 4:50 PM)
                if (formattedDateTime != null) {
                    val dateText = if (isOverdue) "Overdue • $formattedDateTime" else formattedDateTime
                    val dateColor = if (isOverdue) Color(0xFFDC2626) else if (task.isDone) Gray400 else Orange

                    Text(
                        text = dateText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = dateColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // Title Tugas
                Text(
                    text = task.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isDone) Gray400 else Ink,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Poin-poin Sub Task (bullet items)
                if (subTasks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        subTasks.take(4).forEach { item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .background(if (task.isDone) Gray300 else Orange, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item,
                                    fontSize = 12.sp,
                                    color = if (task.isDone) Gray400 else InkSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (subTasks.size > 4) {
                            Text(
                                text = "+ ${subTasks.size - 4} lainnya",
                                fontSize = 11.sp,
                                color = InkSoft,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Action Edit & Hapus di sisi kanan
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = InkSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Hapus",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Detail View Dialog
 * Menampilkan rincian Status, Prioritas, Sub-tasks, dan info lengkap tugas.
 */
@Composable
private fun TaskDetailDialog(
    task: TaskEntity,
    onDismiss: () -> Unit,
    onCycleStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val currentStatus = if (task.isDone) TaskStatus.DONE else TaskStatus.fromCode(task.status)
    val priority = TaskPriority.fromLevel(task.priority)
    val subTasks = task.subTaskList

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Detail Tugas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Ink
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", tint = InkSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Judul
                Text(
                    text = task.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )

                Divider(color = Gray200)

                // Status & Prioritas di Detail View
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = when (currentStatus) {
                            TaskStatus.TODO -> Color(0xFFEFF6FF)
                            TaskStatus.IN_PROGRESS -> OrangeSoft
                            TaskStatus.DONE -> Color(0xFFECFDF5)
                        },
                        border = BorderStroke(
                            1.dp,
                            when (currentStatus) {
                                TaskStatus.TODO -> Color(0xFFBFDBFE)
                                TaskStatus.IN_PROGRESS -> Orange
                                TaskStatus.DONE -> Color(0xFFA7F3D0)
                            }
                        ),
                        modifier = Modifier.clickable { onCycleStatus() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Status: ${currentStatus.label}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = when (currentStatus) {
                                    TaskStatus.TODO -> Color(0xFF1D4ED8)
                                    TaskStatus.IN_PROGRESS -> Orange
                                    TaskStatus.DONE -> Color(0xFF047857)
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Ganti Status",
                                modifier = Modifier.size(13.dp),
                                tint = Orange
                            )
                        }
                    }

                    // Priority Pill
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = priority.badgeBg,
                        border = BorderStroke(1.dp, priority.color.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "Prioritas: ${priority.label}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = priority.color,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Waktu Tenggat
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Orange,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = TaskDateFormatter.formatDetailDateTime(task.dueAt),
                        fontSize = 13.sp,
                        color = InkSecondary
                    )
                }

                // Sub-tasks checklist
                if (subTasks.isNotEmpty()) {
                    Text(
                        text = "Daftar Sub-tugas:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        subTasks.forEach { subItem ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Orange,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = subItem,
                                    fontSize = 13.sp,
                                    color = Ink
                                )
                            }
                        }
                    }
                }

                // Catatan
                if (!task.note.isNullOrBlank()) {
                    Text(
                        text = "Catatan:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink
                    )
                    Text(
                        text = task.note,
                        fontSize = 13.sp,
                        color = InkSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Orange)
                ) {
                    Text("Edit", color = Orange, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Orange)
                ) {
                    Text("Tutup", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text("Hapus", color = Color(0xFFDC2626), fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

/**
 * Dialog Form Tambah / Edit Tugas
 * Dilengkapi dengan Input Kalender (DatePicker) & Jam (TimePicker),
 * serta input poin-poin sub-task.
 */
@Composable
private fun TaskFormDialog(
    title: String,
    initialTask: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, note: String?, subTasks: String?, priority: Int, status: String, dueAt: Long?) -> Unit
) {
    val context = LocalContext.current
    var taskTitle by remember { mutableStateOf(initialTask?.title ?: "") }
    var taskNote by remember { mutableStateOf(initialTask?.note ?: "") }
    var selectedPriority by remember { mutableStateOf(initialTask?.priority ?: 1) }
    var selectedStatus by remember { mutableStateOf(initialTask?.status ?: "TODO") }
    var selectedDueAt by remember { mutableStateOf<Long?>(initialTask?.dueAt) }

    // Sub-tasks state
    val existingSubTasks = remember(initialTask) {
        initialTask?.subTaskList?.toMutableStateList() ?: mutableStateListOf()
    }
    var newSubTaskInput by remember { mutableStateOf("") }

    // Kalender (DatePickerDialog)
    val calendar = Calendar.getInstance().apply {
        if (selectedDueAt != null) timeInMillis = selectedDueAt!!
    }

    val openDatePicker = {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    if (selectedDueAt != null) timeInMillis = selectedDueAt!!
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                selectedDueAt = cal.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // Waktu (TimePickerDialog)
    val openTimePicker = {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val cal = Calendar.getInstance().apply {
                    if (selectedDueAt != null) timeInMillis = selectedDueAt!!
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                }
                selectedDueAt = cal.timeInMillis
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24 hour
        ).show()
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

                // Input Kalender dan Waktu
                Text(
                    text = "Tenggat Waktu (Kalender & Jam)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InkSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tombol Kalender
                    OutlinedButton(
                        onClick = openDatePicker,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (selectedDueAt != null) Orange else Gray300)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Pilih Tanggal",
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedDueAt != null) Orange else InkSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = TaskDateFormatter.formatDateOnly(selectedDueAt),
                            fontSize = 12.sp,
                            color = if (selectedDueAt != null) Ink else InkSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Tombol Jam/Waktu
                    OutlinedButton(
                        onClick = openTimePicker,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (selectedDueAt != null) Orange else Gray300)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Pilih Waktu",
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedDueAt != null) Orange else InkSecondary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = TaskDateFormatter.formatTimeOnly(selectedDueAt),
                            fontSize = 12.sp,
                            color = if (selectedDueAt != null) Ink else InkSecondary
                        )
                    }
                }

                // Tombol Hapus Tenggat
                if (selectedDueAt != null) {
                    TextButton(
                        onClick = { selectedDueAt = null },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Hapus Tenggat Waktu", fontSize = 11.sp, color = Color(0xFFDC2626))
                    }
                }

                Divider(color = Gray200)

                // Input Poin-poin Sub-task (misal: Pick up bag, Rice, Meat)
                Text(
                    text = "Poin-poin Sub-tugas",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InkSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newSubTaskInput,
                        onValueChange = { newSubTaskInput = it },
                        placeholder = { Text("Tambah poin (mis: Rice)...", fontSize = 13.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (newSubTaskInput.isNotBlank()) {
                                existingSubTasks.add(newSubTaskInput.trim())
                                newSubTaskInput = ""
                            }
                        },
                        modifier = Modifier
                            .background(Orange, RoundedCornerShape(10.dp))
                            .size(46.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Poin",
                            tint = Color.White
                        )
                    }
                }

                // List Sub-tasks yang sudah dimasukkan
                if (existingSubTasks.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        existingSubTasks.forEachIndexed { index, subItem ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(Orange, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = subItem,
                                        fontSize = 13.sp,
                                        color = Ink
                                    )
                                }
                                IconButton(
                                    onClick = { existingSubTasks.removeAt(index) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hapus Poin",
                                        tint = Gray500,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Divider(color = Gray200)

                // Prioritas Tugas
                Text(
                    text = "Prioritas",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InkSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        TaskPriority.LOW,
                        TaskPriority.MEDIUM,
                        TaskPriority.HIGH
                    ).forEach { p ->
                        val isSelected = selectedPriority == p.level
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedPriority = p.level },
                            label = { Text(p.label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = p.badgeBg,
                                selectedLabelColor = p.color
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) p.color else Gray200
                            )
                        )
                    }
                }

                // Status Tugas
                Text(
                    text = "Status",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InkSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        TaskStatus.TODO,
                        TaskStatus.IN_PROGRESS,
                        TaskStatus.DONE
                    ).forEach { s ->
                        val isSelected = selectedStatus == s.code
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedStatus = s.code },
                            label = { Text(s.label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = OrangeSoft,
                                selectedLabelColor = Orange
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Orange else Gray200
                            )
                        )
                    }
                }

                // Catatan
                OutlinedTextField(
                    value = taskNote,
                    onValueChange = { taskNote = it },
                    label = { Text("Catatan Tambahan (Opsional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        val subTasksText = if (existingSubTasks.isEmpty()) null
                        else existingSubTasks.joinToString("\n")

                        onSave(
                            taskTitle.trim(),
                            taskNote.trim().ifBlank { null },
                            subTasksText,
                            selectedPriority,
                            selectedStatus,
                            selectedDueAt
                        )
                    }
                },
                enabled = taskTitle.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = InkSecondary)
            }
        }
    )
}

/**
 * Dialog Konfirmasi Hapus Tugas
 */
@Composable
private fun DeleteConfirmationDialog(
    taskTitle: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = null,
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(36.dp)
            )
        },
        title = { Text("Hapus Tugas", fontWeight = FontWeight.Bold, color = Ink) },
        text = {
            Text(
                "Apakah Anda yakin ingin menghapus tugas \"$taskTitle\"? Tindakan ini tidak dapat dibatalkan.",
                color = InkSecondary,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Hapus", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = InkSecondary)
            }
        }
    )
}
