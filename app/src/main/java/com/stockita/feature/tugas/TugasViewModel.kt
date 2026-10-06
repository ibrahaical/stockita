package com.stockita.feature.tugas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockita.core.database.entity.TaskEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TugasUiState {
    object Loading : TugasUiState
    data class Success(val tasks: List<TaskEntity>) : TugasUiState
    data class Error(val message: String) : TugasUiState
}

@HiltViewModel
class TugasViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    val uiState: StateFlow<TugasUiState> = taskRepository.getAllTasks()
        .map { TugasUiState.Success(it) }
        .catch { TugasUiState.Error(it.message ?: "Terjadi kesalahan") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TugasUiState.Loading
        )

    fun addTask(title: String, note: String?) {
        viewModelScope.launch {
            taskRepository.insertTask(
                TaskEntity(
                    title = title,
                    note = note,
                    dueAt = null, // Simplified for MVP without DatePicker
                    isDone = false
                )
            )
        }
    }

    fun toggleTaskDone(task: TaskEntity, isDone: Boolean) {
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isDone = isDone))
        }
    }
}
