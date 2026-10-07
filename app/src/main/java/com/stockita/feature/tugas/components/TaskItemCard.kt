package com.stockita.feature.tugas.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockita.core.database.entity.TaskEntity
import com.stockita.feature.tugas.model.TaskDateFormatter
import com.stockita.feature.tugas.model.TaskTag
import com.stockita.ui.theme.*

/**
 * Kartu Item Tugas (design.md §8.4, §8.2, §2.4, §12.2):
 * - Latar gradient pastel halus: aksen-bg -> #FFFFFF (90 derajat)
 * - Aksen kiri 3.5 px warna label kategori
 * - Radius r-lg (18 px)
 * - Checkbox bulat 22 px di kanan (touch area 36 px)
 * - Didukung SwipeToDismissBox (Swipe kanan = selesai, Swipe kiri = hapus)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSwipeableItemCard(
    task: TaskEntity,
    onCardClick: () -> Unit,
    onToggleDone: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> {
                onToggleDone(!task.isDone)
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
            SwipeToDismissBoxValue.EndToStart -> {
                onDeleteClick()
                dismissState.snapTo(SwipeToDismissBoxValue.Settled)
            }
            SwipeToDismissBoxValue.Settled -> Unit
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        modifier = modifier,
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val isSwipingRight = direction == SwipeToDismissBoxValue.StartToEnd
            val isSwipingLeft = direction == SwipeToDismissBoxValue.EndToStart

            val bgColor by animateColorAsState(
                targetValue = when {
                    isSwipingRight -> SuccessBg
                    isSwipingLeft -> DangerBg
                    else -> Color.Transparent
                },
                label = "swipeBg"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(bgColor)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (isSwipingRight) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (isSwipingRight) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selesai",
                            tint = SuccessText,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (task.isDone) "Batal Selesai" else "Selesai",
                            color = SuccessText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                } else if (isSwipingLeft) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hapus",
                            color = DangerText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = DangerText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    ) {
        TaskItemCard(
            task = task,
            onCardClick = onCardClick,
            onToggleDone = onToggleDone,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick
        )
    }
}

/**
 * Konten Visual Kartu Tugas
 */
@Composable
fun TaskItemCard(
    task: TaskEntity,
    onCardClick: () -> Unit,
    onToggleDone: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tag = TaskTag.fromRefType(task.refType)
    val formattedDateTime = TaskDateFormatter.formatCardDateTime(task.dueAt)
    val isOverdue = TaskDateFormatter.isOverdue(task.dueAt, task.isDone)
    val subTasks = task.subTaskList

    // Gradient halus 90 derajat aksen pastel -> putih
    val cardBrush = remember(tag, task.isDone) {
        if (task.isDone) {
            Brush.horizontalGradient(listOf(SurfaceMuted, Color.White))
        } else {
            Brush.horizontalGradient(
                listOf(
                    tag.bg.copy(alpha = 0.55f),
                    Color.White
                )
            )
        }
    }

    Surface(
        onClick = onCardClick,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Border),
        shadowElevation = 1.dp,
        color = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBrush)
        ) {
            // Aksen garis kiri 3.5 px
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .background(if (task.isDone) TextDisabled else tag.color)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, top = 12.dp, end = 14.dp, bottom = 12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Konten Kiri (Kategori, Judul, Meta, Subtasks)
                Column(modifier = Modifier.weight(1f)) {
                    // Header Baris: Badge Kategori, Prioritas & Waktu
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Badge Kategori Pastel
                        Surface(
                            shape = CircleShape,
                            color = if (task.isDone) SurfaceMuted else tag.bg
                        ) {
                            Text(
                                text = tag.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (task.isDone) TextTertiary else tag.color,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        // Badge Prioritas Tinggi
                        if (task.priority == 2 && !task.isDone) {
                            Surface(
                                shape = CircleShape,
                                color = DangerBg
                            ) {
                                Text(
                                    text = "Tinggi",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DangerText,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Meta Tanggal / Overdue
                        if (formattedDateTime != null) {
                            Text(
                                text = "•",
                                fontSize = 10.sp,
                                color = TextTertiary
                            )
                            Text(
                                text = if (isOverdue) "Terlambat • $formattedDateTime" else formattedDateTime,
                                fontSize = 11.sp,
                                fontWeight = if (isOverdue) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isOverdue) DangerText else if (task.isDone) TextDisabled else TextTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Judul Tugas
                    Text(
                        text = task.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.isDone) TextTertiary else TextPrimary,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Sub-tasks checklist kecil jika ada
                    if (subTasks.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(5.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            subTasks.take(3).forEach { subItem ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(if (task.isDone) TextDisabled else tag.color, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = subItem,
                                        fontSize = 11.sp,
                                        color = if (task.isDone) TextDisabled else TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            if (subTasks.size > 3) {
                                Text(
                                    text = "+ ${subTasks.size - 3} poin lainnya",
                                    fontSize = 10.sp,
                                    color = TextTertiary,
                                    modifier = Modifier.padding(start = 11.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Kontrol Kanan: Checkbox bulat 22 px & Tombol Aksi ringkas
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Checkbox bulat 22 px (§8.2: kosong border 1.5px border-strong, tercentang primary-600)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { onToggleDone(!task.isDone) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isDone) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(Primary600, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selesai",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .border(BorderStroke(1.5.dp, BorderStrong), CircleShape)
                                    .background(Color.White, CircleShape)
                            )
                        }
                    }

                    // Aksi cepat: Edit & Hapus
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        IconButton(
                            onClick = onEditClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = TextTertiary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        IconButton(
                            onClick = onDeleteClick,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Hapus",
                                tint = Danger,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
