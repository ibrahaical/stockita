package com.stockita.feature.tugas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockita.core.database.entity.TaskEntity
import com.stockita.feature.tugas.model.TaskCategory
import com.stockita.feature.tugas.model.TaskDateFormatter
import com.stockita.feature.tugas.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TugasUiState(
    val isLoading: Boolean = false,
    val tasks: List<TaskEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: TaskCategory = TaskCategory.TODAY,
    val totalCount: Int = 0,
    val todayCount: Int = 0,
    val scheduledCount: Int = 0,
    val allCount: Int = 0,
    val overdueCount: Int = 0,
    val completedCount: Int = 0,
    val error: String? = null
) {
    val progressPercentage: Int
        get() = if (totalCount > 0) (completedCount.toFloat() / totalCount * 100).toInt() else 0
}

@HiltViewModel
class TugasViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow(TaskCategory.TODAY)
    val selectedCategory: StateFlow<TaskCategory> = _selectedCategory

    val uiState: StateFlow<TugasUiState> = combine(
        taskRepository.getAllTasks(),
        _searchQuery,
        _selectedCategory
    ) { allTasks, query, category ->
        val todayCount = allTasks.count { TaskDateFormatter.isToday(it.dueAt) && !it.isDone }
        val scheduledCount = allTasks.count { TaskDateFormatter.isScheduled(it.dueAt) && !it.isDone }
        val allCount = allTasks.count { !it.isDone }
        val overdueCount = allTasks.count { TaskDateFormatter.isOverdue(it.dueAt, it.isDone) }
        val completedCount = allTasks.count { it.isDone }

        val categoryFiltered = when (category) {
            TaskCategory.TODAY -> allTasks.filter { TaskDateFormatter.isToday(it.dueAt) }
            TaskCategory.SCHEDULED -> allTasks.filter { TaskDateFormatter.isScheduled(it.dueAt) }
            TaskCategory.ALL -> allTasks
            TaskCategory.OVERDUE -> allTasks.filter { TaskDateFormatter.isOverdue(it.dueAt, it.isDone) }
        }

        val searchFiltered = categoryFiltered.filter { task ->
            if (query.isBlank()) true
            else {
                task.title.contains(query, ignoreCase = true) ||
                        (task.note?.contains(query, ignoreCase = true) == true) ||
                        (task.subTasks?.contains(query, ignoreCase = true) == true)
            }
        }

        TugasUiState(
            isLoading = false,
            tasks = searchFiltered,
            searchQuery = query,
            selectedCategory = category,
            totalCount = allTasks.size,
            todayCount = todayCount,
            scheduledCount = scheduledCount,
            allCount = allCount,
            overdueCount = overdueCount,
            completedCount = completedCount
        )
    }.catch {
        emit(TugasUiState(error = it.message ?: "Terjadi kesalahan"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TugasUiState(isLoading = true)
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: TaskCategory) {
        _selectedCategory.value = category
    }

    fun addTask(
        title: String,
        note: String? = null,
        subTasks: String? = null,
        priority: Int = 1,
        status: String = "TODO",
        dueAt: Long? = null
    ) {
        viewModelScope.launch {
            val isDone = (status == "DONE")
            taskRepository.insertTask(
                TaskEntity(
                    title = title,
                    note = note,
                    subTasks = subTasks,
                    dueAt = dueAt,
                    isDone = isDone,
                    priority = priority,
                    status = status
                )
            )
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun cycleTaskStatus(task: TaskEntity) {
        viewModelScope.launch {
            val currentStatus = if (task.isDone) TaskStatus.DONE else TaskStatus.fromCode(task.status)
            val next = currentStatus.nextStatus()
            val newIsDone = (next == TaskStatus.DONE)
            taskRepository.updateTask(
                task.copy(
                    status = next.code,
                    isDone = newIsDone
                )
            )
        }
    }

    fun toggleTaskDone(task: TaskEntity, isDone: Boolean) {
        viewModelScope.launch {
            val newStatus = if (isDone) TaskStatus.DONE.code else TaskStatus.TODO.code
            taskRepository.updateTask(
                task.copy(
                    isDone = isDone,
                    status = newStatus
                )
            )
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}

