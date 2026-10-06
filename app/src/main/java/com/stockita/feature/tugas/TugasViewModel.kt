package com.stockita.feature.tugas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockita.core.database.entity.TaskEntity
import com.stockita.feature.tugas.model.TaskPriority
import com.stockita.feature.tugas.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TugasUiState(
    val isLoading: Boolean = false,
    val tasks: List<TaskEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedStatusTab: TaskStatus? = null, // null means "Semua"
    val selectedPriorityFilter: TaskPriority? = null, // null means "Semua"
    val totalCount: Int = 0,
    val todoCount: Int = 0,
    val inProgressCount: Int = 0,
    val doneCount: Int = 0,
    val error: String? = null
) {
    val progressPercentage: Int
        get() = if (totalCount > 0) (doneCount.toFloat() / totalCount * 100).toInt() else 0
}

@HiltViewModel
class TugasViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedStatusTab = MutableStateFlow<TaskStatus?>(null)
    val selectedStatusTab: StateFlow<TaskStatus?> = _selectedStatusTab

    private val _selectedPriorityFilter = MutableStateFlow<TaskPriority?>(null)
    val selectedPriorityFilter: StateFlow<TaskPriority?> = _selectedPriorityFilter

    val uiState: StateFlow<TugasUiState> = combine(
        taskRepository.getAllTasks(),
        _searchQuery,
        _selectedStatusTab,
        _selectedPriorityFilter
    ) { allTasks, query, statusTab, priorityFilter ->
        val todoCount = allTasks.count { TaskStatus.fromCode(it.status) == TaskStatus.TODO && !it.isDone }
        val inProgressCount = allTasks.count { TaskStatus.fromCode(it.status) == TaskStatus.IN_PROGRESS && !it.isDone }
        val doneCount = allTasks.count { it.isDone || TaskStatus.fromCode(it.status) == TaskStatus.DONE }

        val filtered = allTasks.filter { task ->
            val taskStatus = if (task.isDone) TaskStatus.DONE else TaskStatus.fromCode(task.status)

            // Search Filter
            val matchesSearch = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    (task.note?.contains(query, ignoreCase = true) == true)

            // Status Tab Filter
            val matchesStatus = (statusTab == null) || (taskStatus == statusTab)

            // Priority Filter
            val matchesPriority = (priorityFilter == null) || (task.priority == priorityFilter.level)

            matchesSearch && matchesStatus && matchesPriority
        }

        TugasUiState(
            isLoading = false,
            tasks = filtered,
            searchQuery = query,
            selectedStatusTab = statusTab,
            selectedPriorityFilter = priorityFilter,
            totalCount = allTasks.size,
            todoCount = todoCount,
            inProgressCount = inProgressCount,
            doneCount = doneCount
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

    fun setStatusTab(status: TaskStatus?) {
        _selectedStatusTab.value = status
    }

    fun setPriorityFilter(priority: TaskPriority?) {
        _selectedPriorityFilter.value = priority
    }

    fun addTask(
        title: String,
        note: String?,
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
