package com.stockita.feature.tugas.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockita.core.database.entity.TaskEntity
import com.stockita.feature.tugas.model.TaskDateFormatter
import com.stockita.feature.tugas.model.TaskPriority
import com.stockita.feature.tugas.model.TaskStatus
import com.stockita.feature.tugas.model.TaskTag
import com.stockita.ui.theme.*

/**
 * Dialog Rincian / Detail Tugas (design.md §8.5 & §8.4):
 * - Menampilkan rincian judul, kategori, tenggat, prioritas, checklist sub-tugas, dan catatan
 * - Aksi ganti status cepat, edit, dan hapus
 */
@Composable
fun TaskDetailDialog(
    task: TaskEntity,
    onDismiss: () -> Unit,
    onCycleStatus: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val tag = TaskTag.fromRefType(task.refType)
    val currentStatus = if (task.isDone) TaskStatus.DONE else TaskStatus.fromCode(task.status)
    val priority = TaskPriority.fromLevel(task.priority)
    val subTasks = task.subTaskList

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceColor,
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 0.dp,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(shape = CircleShape, color = tag.bg) {
                        Text(
                            text = tag.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = tag.color,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = "Rincian Tugas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = TextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Judul
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                HorizontalDivider(color = Border, thickness = 1.dp)

                // Status & Prioritas Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status Pill (bisa di-tap untuk cycle status)
                    Surface(
                        shape = CircleShape,
                        color = when (currentStatus) {
                            TaskStatus.TODO -> Primary50
                            TaskStatus.IN_PROGRESS -> WarningBg
                            TaskStatus.DONE -> SuccessBg
                        },
                        border = BorderStroke(
                            1.dp,
                            when (currentStatus) {
                                TaskStatus.TODO -> Primary200
                                TaskStatus.IN_PROGRESS -> Warning
                                TaskStatus.DONE -> Success
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
                                    TaskStatus.TODO -> Primary700
                                    TaskStatus.IN_PROGRESS -> WarningText
                                    TaskStatus.DONE -> SuccessText
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Primary600
                            )
                        }
                    }

                    // Priority Pill
                    Surface(
                        shape = CircleShape,
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

                // Tenggat Waktu
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Primary600,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = TaskDateFormatter.formatDetailDateTime(task.dueAt),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Sub-tugas checklist
                if (subTasks.isNotEmpty()) {
                    Text(
                        text = "Sub-tugas:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceMuted, RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        subTasks.forEach { subItem ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Primary600,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = subItem,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                // Catatan
                if (!task.note.isNullOrBlank()) {
                    Text(
                        text = "Catatan:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = task.note,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceMuted, RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    onClick = onEdit,
                    shape = CircleShape,
                    color = Primary50,
                    border = BorderStroke(1.dp, Primary200),
                    modifier = Modifier.height(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp)) {
                        Text("Edit", color = Primary700, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }

                Surface(
                    onClick = onDismiss,
                    shape = CircleShape,
                    color = Primary600,
                    modifier = Modifier.height(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 14.dp)) {
                        Text("Tutup", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDelete) {
                Text("Hapus", color = Danger, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
            }
        }
    )
}
