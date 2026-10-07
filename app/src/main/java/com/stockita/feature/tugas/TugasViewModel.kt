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

/**
 * Representasi State UI untuk Layar Tugas.
 *
 * @property isLoading Menandakan data sedang dimuat pertama kali.
 * @property tasks Daftar tugas hasil filter kategori & pencarian.
 * @property searchQuery Kata kunci pencarian pengguna.
 * @property selectedCategory Filter tab kategori aktif (TODAY, SCHEDULED, DONE, ALL).
 * @property totalCount Total keseluruhan tugas di database.
 * @property todayCount Jumlah tugas aktif hari ini.
 * @property scheduledCount Jumlah tugas aktif mendatang.
 * @property doneCount Jumlah tugas yang telah selesai.
 * @property allCount Total tugas aktif maupun selesai.
 * @property overdueCount Jumlah tugas aktif yang melewati tenggat waktu.
 * @property completedCount Jumlah tugas dengan status selesai.
 * @property error Pesan kesalahan jika terjadi kegagalan operasi.
 */
data class TugasUiState(
    val isLoading: Boolean = false,
    val tasks: List<TaskEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: TaskCategory = TaskCategory.TODAY,
    val totalCount: Int = 0,
    val todayCount: Int = 0,
    val scheduledCount: Int = 0,
    val doneCount: Int = 0,
    val allCount: Int = 0,
    val overdueCount: Int = 0,
    val completedCount: Int = 0,
    val error: String? = null
) {
    /**
     * Persentase penyelesaian tugas (0 - 100%).
     */
    val progressPercentage: Int
        get() = if (totalCount > 0) (completedCount.toFloat() / totalCount * 100).toInt() else 0
}

/**
 * ViewModel untuk mengelola state dan operasi bisnis modul Tugas / To-Do.
 *
 * Menyediakan:
 * - Reactive flow kombinasi antara Room database, query pencarian, dan filter kategori.
 * - Operasi CRUD: tambah tugas, perbarui tugas, hapus tugas.
 * - Toggle cepat status tugas (centang / batalkan centang).
 * - Rotasi status berurutan (TODO -> IN_PROGRESS -> DONE).
 */
@HiltViewModel
class TugasViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow(TaskCategory.TODAY)
    val selectedCategory: StateFlow<TaskCategory> = _selectedCategory

    /**
     * Aliran data UI utama yang menggabungkan tasks dari database Room,
     * filter pencarian, dan filter kategori secara reaktif.
     */
    val uiState: StateFlow<TugasUiState> = combine(
        taskRepository.getAllTasks(),
        _searchQuery,
        _selectedCategory
    ) { allTasks, query, category ->
        val todayCount = allTasks.count { (TaskDateFormatter.isToday(it.dueAt) || it.dueAt == null) && !it.isDone }
        val scheduledCount = allTasks.count { TaskDateFormatter.isScheduled(it.dueAt) && !it.isDone }
        val completedCount = allTasks.count { it.isDone || it.status == "DONE" }
        val allCount = allTasks.size
        val overdueCount = allTasks.count { TaskDateFormatter.isOverdue(it.dueAt, it.isDone) }

        val categoryFiltered = when (category) {
            TaskCategory.TODAY -> allTasks.filter { (TaskDateFormatter.isToday(it.dueAt) || it.dueAt == null) && !it.isDone }
            TaskCategory.SCHEDULED -> allTasks.filter { TaskDateFormatter.isScheduled(it.dueAt) && !it.isDone }
            TaskCategory.DONE -> allTasks.filter { it.isDone || it.status == "DONE" }
            TaskCategory.ALL -> allTasks
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
            doneCount = completedCount,
            allCount = allCount,
            overdueCount = overdueCount,
            completedCount = completedCount
        )
    }.catch { throwable ->
        emit(TugasUiState(error = throwable.message ?: "Terjadi kesalahan saat memuat data"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TugasUiState(isLoading = true)
    )

    /**
     * Memperbarui filter kata kunci pencarian.
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Memilih filter tab kategori (Hari Ini, Mendatang, Selesai, Semua).
     */
    fun selectCategory(category: TaskCategory) {
        _selectedCategory.value = category
    }

    /**
     * Menambahkan tugas baru ke dalam database.
     */
    fun addTask(
        title: String,
        note: String? = null,
        subTasks: String? = null,
        priority: Int = 1,
        status: String = "TODO",
        dueAt: Long? = null,
        refType: String? = null
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
                    status = status,
                    refType = refType
                )
            )
        }
    }

    /**
     * Memperbarui data tugas yang sudah ada.
     */
    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    /**
     * Memutar status tugas secara berurutan: TODO -> IN_PROGRESS -> DONE -> TODO.
     */
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

    /**
     * Menandai tugas selesai atau batal selesai (misal saat checkbox atau swipe diklik).
     */
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

    /**
     * Menghapus tugas dari database.
     */
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }
}
