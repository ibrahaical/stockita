package com.stockita.feature.tugas.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockita.core.database.entity.TaskEntity
import com.stockita.feature.tugas.model.TaskDateFormatter
import com.stockita.feature.tugas.model.TaskPriority
import com.stockita.feature.tugas.model.TaskStatus
import com.stockita.feature.tugas.model.TaskTag
import com.stockita.ui.theme.*
import java.util.Calendar
import java.util.TimeZone

/**
 * Bottom Sheet Form Tambah / Edit Tugas (design.md §8.5 & §12.2):
 * - Wadah: ModalBottomSheet dengan sudut atas r-2xl (28 px)
 * - Handle: 36×4 dp BorderStrong di tengah atas
 * - Selector Chip Kategori Pastel (§2.4)
 * - Input Judul: Pil r-full tinggi 44–52 dp (§8.2)
 * - Pemilih Tanggal & Jam dengan Material 3 Pickers
 * - Toggle Pengingat Tenggat 40×24 dp (§8.2)
 * - Checklist Sub-tugas interaktif
 * - Pil Selector Prioritas & Status
 * - Input Catatan: Multiline r-md 14 px (§8.2)
 * - Sticky Action CTA: Tombol Batal & Simpan Tugas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormBottomSheet(
    initialTask: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        note: String?,
        subTasks: String?,
        priority: Int,
        status: String,
        dueAt: Long?,
        refType: String?
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var taskTitle by remember { mutableStateOf(initialTask?.title ?: "") }
    var taskNote by remember { mutableStateOf(initialTask?.note ?: "") }
    var selectedPriority by remember { mutableStateOf(initialTask?.priority ?: 1) }
    var selectedStatus by remember { mutableStateOf(initialTask?.status ?: "TODO") }
    var selectedDueAt by remember { mutableStateOf<Long?>(initialTask?.dueAt) }
    var reminderEnabled by remember { mutableStateOf(initialTask?.dueAt != null) }
    var selectedTag by remember { mutableStateOf(TaskTag.fromRefType(initialTask?.refType)) }

    val existingSubTasks = remember(initialTask) {
        initialTask?.subTaskList?.toMutableStateList() ?: mutableStateListOf()
    }
    var newSubTaskInput by remember { mutableStateOf("") }

    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance().apply {
        if (selectedDueAt != null) timeInMillis = selectedDueAt!!
    }

    // Modal Date Picker Material 3 (Violet themed)
    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDueAt ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { utcMillis ->
                            val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = utcMillis
                            }
                            val localCal = Calendar.getInstance().apply {
                                if (selectedDueAt != null) timeInMillis = selectedDueAt!!
                                set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
                                set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
                                set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
                            }
                            selectedDueAt = localCal.timeInMillis
                        }
                        showDatePickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary600),
                    shape = CircleShape
                ) {
                    Text("Pilih Tanggal", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Batal", color = TextSecondary, fontSize = 13.sp)
                }
            },
            colors = DatePickerDefaults.colors(containerColor = SurfaceColor),
            shape = RoundedCornerShape(24.dp)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = SurfaceColor,
                    titleContentColor = TextPrimary,
                    headlineContentColor = Primary600,
                    weekdayContentColor = TextSecondary,
                    subheadContentColor = TextPrimary,
                    yearContentColor = TextPrimary,
                    currentYearContentColor = Primary600,
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = Primary600,
                    dayContentColor = TextPrimary,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = Primary600,
                    todayContentColor = Primary600,
                    todayDateBorderColor = Primary600
                )
            )
        }
    }

    // Modal Time Picker Material 3 (Violet themed)
    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        val cal = Calendar.getInstance().apply {
                            if (selectedDueAt != null) timeInMillis = selectedDueAt!!
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                        }
                        selectedDueAt = cal.timeInMillis
                        showTimePickerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary600),
                    shape = CircleShape
                ) {
                    Text("Pilih Jam", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text("Batal", color = TextSecondary, fontSize = 13.sp)
                }
            },
            containerColor = SurfaceColor,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Tentukan Jam Tenggat", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = SurfaceMuted,
                            clockDialSelectedContentColor = Color.White,
                            clockDialUnselectedContentColor = TextPrimary,
                            selectorColor = Primary600,
                            periodSelectorBorderColor = Border,
                            periodSelectorSelectedContainerColor = Primary100,
                            periodSelectorUnselectedContainerColor = SurfaceMuted,
                            periodSelectorSelectedContentColor = Primary700,
                            periodSelectorUnselectedContentColor = TextSecondary,
                            timeSelectorSelectedContainerColor = Primary100,
                            timeSelectorUnselectedContainerColor = SurfaceMuted,
                            timeSelectorSelectedContentColor = Primary700,
                            timeSelectorUnselectedContentColor = TextPrimary
                        )
                    )
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceColor,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            // Handle bar 36×4 border-strong
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(BorderStrong, CircleShape)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Sheet (title 16/600 & tombol tutup bulat 32px)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (initialTask != null) "Edit Tugas" else "Tambah Tugas Baru",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Surface(
                    onClick = onDismiss,
                    shape = CircleShape,
                    color = SurfaceMuted,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 1. Kategori Tag Chip Selector (§2.4)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Kategori Tugas",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskTag.entries.forEach { tagOption ->
                        val isSelected = selectedTag == tagOption
                        Surface(
                            onClick = { selectedTag = tagOption },
                            shape = CircleShape,
                            color = if (isSelected) tagOption.bg else SurfaceMuted,
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) tagOption.color else Border
                            ),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(tagOption.color, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = tagOption.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                    color = if (isSelected) tagOption.color else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 2. Input Judul Tugas (Pill r-full, tinggi 44–52px §8.2)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Judul Tugas *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    placeholder = { Text("Nama atau target pekerjaan...", fontSize = 13.sp, color = TextTertiary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary500,
                        unfocusedBorderColor = Border,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = SurfaceColor,
                        unfocusedContainerColor = SurfaceColor
                    )
                )
            }

            // 3. Tenggat Waktu (Kalender & Jam)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Tenggat Waktu",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tombol Tanggal Pill
                    Surface(
                        onClick = { showDatePickerDialog = true },
                        shape = CircleShape,
                        color = if (selectedDueAt != null) Primary50 else SurfaceMuted,
                        border = BorderStroke(1.dp, if (selectedDueAt != null) Primary200 else Border),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = if (selectedDueAt != null) Primary600 else TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = TaskDateFormatter.formatDateOnly(selectedDueAt),
                                fontSize = 12.sp,
                                fontWeight = if (selectedDueAt != null) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selectedDueAt != null) Primary700 else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Tombol Jam Pill
                    Surface(
                        onClick = { showTimePickerDialog = true },
                        shape = CircleShape,
                        color = if (selectedDueAt != null) Primary50 else SurfaceMuted,
                        border = BorderStroke(1.dp, if (selectedDueAt != null) Primary200 else Border),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = if (selectedDueAt != null) Primary600 else TextTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = TaskDateFormatter.formatTimeOnly(selectedDueAt),
                                fontSize = 12.sp,
                                fontWeight = if (selectedDueAt != null) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selectedDueAt != null) Primary700 else TextSecondary
                            )
                        }
                    }
                }

                if (selectedDueAt != null) {
                    Row(
                        modifier = Modifier
                            .clickable { selectedDueAt = null }
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Danger,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Hapus tenggat waktu",
                            fontSize = 11.sp,
                            color = DangerText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 4. Pengingat Toggle (design.md §12.2 & §8.2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceMuted, RoundedCornerShape(14.dp))
                    .border(BorderStroke(1.dp, Border), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(if (reminderEnabled) Primary100 else SurfaceColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (reminderEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = if (reminderEnabled) Primary600 else TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Pengingat Tugas",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (reminderEnabled) "Aktifkan notifikasi saat tenggat waktu" else "Pengingat dinonaktifkan",
                            fontSize = 11.sp,
                            color = if (reminderEnabled) Primary700 else TextTertiary
                        )
                    }
                }
                StockitaToggle(
                    checked = reminderEnabled,
                    onCheckedChange = { reminderEnabled = it }
                )
            }

            // 5. Checklist Sub-tugas
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sub-tugas (Checklist)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                    if (existingSubTasks.isNotEmpty()) {
                        Text(
                            text = "${existingSubTasks.size} poin",
                            fontSize = 11.sp,
                            color = Primary600,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newSubTaskInput,
                        onValueChange = { newSubTaskInput = it },
                        placeholder = { Text("Tambah item checklist...", fontSize = 12.sp, color = TextTertiary) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary500,
                            unfocusedBorderColor = Border,
                            focusedContainerColor = SurfaceColor,
                            unfocusedContainerColor = SurfaceColor
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        onClick = {
                            if (newSubTaskInput.isNotBlank()) {
                                existingSubTasks.add(newSubTaskInput.trim())
                                newSubTaskInput = ""
                            }
                        },
                        shape = CircleShape,
                        color = Primary600,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tambah Poin",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (existingSubTasks.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceMuted, RoundedCornerShape(14.dp))
                            .border(BorderStroke(1.dp, Border), RoundedCornerShape(14.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        existingSubTasks.forEachIndexed { index, subItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
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
                                            .background(selectedTag.color, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = subItem,
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                }
                                IconButton(
                                    onClick = { existingSubTasks.removeAt(index) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Hapus",
                                        tint = TextTertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Prioritas & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Prioritas
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Prioritas", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(TaskPriority.LOW, TaskPriority.MEDIUM, TaskPriority.HIGH).forEach { p ->
                            val isSelected = selectedPriority == p.level
                            Surface(
                                onClick = { selectedPriority = p.level },
                                shape = CircleShape,
                                color = if (isSelected) p.badgeBg else SurfaceMuted,
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) p.color else Border
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = p.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) p.color else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Status
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Status", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.DONE).forEach { s ->
                            val isSelected = selectedStatus == s.code
                            val activeBg = when (s) {
                                TaskStatus.TODO -> Primary50
                                TaskStatus.IN_PROGRESS -> WarningBg
                                TaskStatus.DONE -> SuccessBg
                            }
                            val activeColor = when (s) {
                                TaskStatus.TODO -> Primary700
                                TaskStatus.IN_PROGRESS -> WarningText
                                TaskStatus.DONE -> SuccessText
                            }
                            Surface(
                                onClick = { selectedStatus = s.code },
                                shape = CircleShape,
                                color = if (isSelected) activeBg else SurfaceMuted,
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) activeColor else Border
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = s.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) activeColor else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 7. Catatan (multiline r-md 14px §8.2)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Catatan Tambahan (Opsional)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )
                OutlinedTextField(
                    value = taskNote,
                    onValueChange = { taskNote = it },
                    placeholder = { Text("Keterangan ringkas atau instruksi...", fontSize = 13.sp, color = TextTertiary) },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary500,
                        unfocusedBorderColor = Border,
                        focusedContainerColor = SurfaceColor,
                        unfocusedContainerColor = SurfaceColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==========================================
            // TOMBOL CTA STICKY BAWAH (design.md §8.1 & §8.5)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Secondary Button "Batal"
                Surface(
                    onClick = onDismiss,
                    shape = CircleShape,
                    color = Primary50,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Batal",
                            color = Primary700,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }

                // Primary Button "Simpan Tugas"
                Surface(
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
                                selectedDueAt,
                                selectedTag.code
                            )
                        }
                    },
                    shape = CircleShape,
                    color = if (taskTitle.isNotBlank()) Primary600 else TextDisabled,
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Simpan Tugas",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
