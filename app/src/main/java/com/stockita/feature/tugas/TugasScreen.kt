package com.stockita.feature.tugas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stockita.core.database.entity.TaskEntity
import com.stockita.feature.tugas.components.*
import com.stockita.ui.theme.*

/**
 * Layar Menu Tugas & To-Do (Pola B sesuai design.md §7.3 & §12.2)
 *
 * Arsitektur Bersih & Modular:
 * - Komponen Header & Progress: [TaskHeader]
 * - Filter Segmented Control: [TaskSegmentedControl]
 * - Bar Pencarian: [TaskSearchBar]
 * - Kartu Item & Swipe Gesture: [TaskSwipeableItemCard]
 * - Tampilan Kosong: [TaskEmptyState]
 * - Form Tambah/Edit: [TaskFormBottomSheet]
 * - Modal Detail: [TaskDetailDialog]
 * - Modal Hapus: [DeleteConfirmationDialog]
 */
@Composable
fun TugasScreen(
    viewModel: TugasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showFormSheet by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }
    var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }
    var taskToDetail by remember { mutableStateOf<TaskEntity?>(null) }

    Scaffold(
        containerColor = BgApp,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            // FAB 48-52px Primary600 dengan shadow-lg (design.md §7.5 & §8.1)
            Surface(
                onClick = {
                    taskToEdit = null
                    showFormSheet = true
                },
                shape = CircleShape,
                color = Primary600,
                shadowElevation = 8.dp,
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Tugas",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(BgApp)
        ) {
            // 1. App Bar & Linear Progress Bar (§7.3 & §8.8)
            TaskHeader(
                completedCount = uiState.completedCount,
                totalCount = uiState.totalCount,
                progressPercentage = uiState.progressPercentage,
                onQuickAddClick = {
                    taskToEdit = null
                    showFormSheet = true
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Segmented Control Filter (§8.6 & §12.2)
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                TaskSegmentedControl(
                    selectedCategory = uiState.selectedCategory,
                    todayCount = uiState.todayCount,
                    scheduledCount = uiState.scheduledCount,
                    doneCount = uiState.doneCount,
                    allCount = uiState.allCount,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Search Bar Pil (§8.2)
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                TaskSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Baris Subjudul & Chip Total Tugas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = uiState.selectedCategory.subtitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )

                Surface(
                    shape = CircleShape,
                    color = Primary100
                ) {
                    Text(
                        text = "${uiState.tasks.size} tugas",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary700,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. Daftar Kartu Tugas atau Tampilan Kosong
            if (uiState.tasks.isEmpty()) {
                TaskEmptyState(
                    isSearching = uiState.searchQuery.isNotBlank(),
                    onAddNewTaskClick = {
                        taskToEdit = null
                        showFormSheet = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskSwipeableItemCard(
                            task = task,
                            onCardClick = { taskToDetail = task },
                            onToggleDone = { viewModel.toggleTaskDone(task, it) },
                            onEditClick = {
                                taskToEdit = task
                                showFormSheet = true
                            },
                            onDeleteClick = { taskToDelete = task }
                        )
                    }
                }
            }
        }
    }

    // Modal Form Tambah / Edit Tugas
    if (showFormSheet) {
        TaskFormBottomSheet(
            initialTask = taskToEdit,
            onDismiss = {
                showFormSheet = false
                taskToEdit = null
            },
            onSave = { title, note, subTasks, priority, status, dueAt, refType ->
                if (taskToEdit == null) {
                    viewModel.addTask(
                        title = title,
                        note = note,
                        subTasks = subTasks,
                        priority = priority,
                        status = status,
                        dueAt = dueAt,
                        refType = refType
                    )
                } else {
                    val isDone = (status == "DONE")
                    viewModel.updateTask(
                        taskToEdit!!.copy(
                            title = title,
                            note = note,
                            subTasks = subTasks,
                            priority = priority,
                            status = status,
                            dueAt = dueAt,
                            isDone = isDone,
                            refType = refType
                        )
                    )
                }
                showFormSheet = false
                taskToEdit = null
            }
        )
    }

    // Modal Detail Tugas
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
                showFormSheet = true
            },
            onDelete = {
                taskToDetail = null
                taskToDelete = task
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
