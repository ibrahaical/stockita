package com.stockita.feature.tugas

import com.stockita.core.database.entity.TaskEntity
import com.stockita.fakes.FakeTaskDao
import com.stockita.feature.tugas.model.TaskDateFormatter
import com.stockita.feature.tugas.model.TaskPriority
import com.stockita.feature.tugas.model.TaskStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class TugasLogicTest {

    private lateinit var fakeTaskDao: FakeTaskDao
    private lateinit var taskRepository: TaskRepository

    @Before
    fun setUp() {
        fakeTaskDao = FakeTaskDao()
        taskRepository = TaskRepository(fakeTaskDao)
    }

    @Test
    fun `insertTask adds new task with default isDone false`() = runTest {
        val task = TaskEntity(
            id = 0L,
            title = "Beli Sirup Vanila",
            note = "Cari di toko grosir ABC",
            dueAt = 1700000000L,
            isDone = false,
            priority = 1
        )

        taskRepository.insertTask(task)

        val tasks = taskRepository.getAllTasks().first()
        assertEquals(1, tasks.size)
        assertEquals("Beli Sirup Vanila", tasks[0].title)
        assertEquals("Cari di toko grosir ABC", tasks[0].note)
        assertFalse(tasks[0].isDone)
        assertEquals(1, tasks[0].priority)
    }

    @Test
    fun `updateTask toggles isDone status correctly`() = runTest {
        val task = TaskEntity(
            id = 1L,
            title = "Periksa persediaan cup",
            isDone = false
        )
        taskRepository.insertTask(task)

        // Toggle to true
        taskRepository.updateTask(task.copy(isDone = true))
        var tasks = taskRepository.getAllTasks().first()
        assertTrue(tasks[0].isDone)

        // Toggle back to false
        taskRepository.updateTask(task.copy(isDone = false))
        tasks = taskRepository.getAllTasks().first()
        assertFalse(tasks[0].isDone)
    }

    @Test
    fun `deleteTask successfully removes task from repository`() = runTest {
        val task = TaskEntity(id = 1L, title = "Task Hapus", isDone = false)
        taskRepository.insertTask(task)

        var tasks = taskRepository.getAllTasks().first()
        assertEquals(1, tasks.size)

        taskRepository.deleteTask(task)
        tasks = taskRepository.getAllTasks().first()
        assertTrue(tasks.isEmpty())
    }

    @Test
    fun `TaskStatus cycle progresses from TODO to IN_PROGRESS to DONE and back to TODO`() {
        assertEquals(TaskStatus.IN_PROGRESS, TaskStatus.TODO.nextStatus())
        assertEquals(TaskStatus.DONE, TaskStatus.IN_PROGRESS.nextStatus())
        assertEquals(TaskStatus.TODO, TaskStatus.DONE.nextStatus())
    }

    @Test
    fun `TaskDateFormatter identifies today scheduled and overdue correctly`() {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 3600_000L

        assertTrue(TaskDateFormatter.isToday(now))
        assertFalse(TaskDateFormatter.isScheduled(now))

        // Tomorrow is scheduled
        val tomorrow = now + 2 * oneDay
        assertTrue(TaskDateFormatter.isScheduled(tomorrow))

        // Past timestamp is overdue
        val yesterday = now - 2 * oneDay
        assertTrue(TaskDateFormatter.isOverdue(yesterday, isDone = false))
        assertFalse(TaskDateFormatter.isOverdue(yesterday, isDone = true))
    }

    @Test
    fun `TaskEntity subTaskList parses newline delimited subtasks accurately`() {
        val taskWithSubTasks = TaskEntity(
            id = 1L,
            title = "Beli Bahan",
            subTasks = "Pick up bag\nRice\nMeat"
        )
        val expected = listOf("Pick up bag", "Rice", "Meat")
        assertEquals(expected, taskWithSubTasks.subTaskList)

        val taskEmptySubTasks = TaskEntity(id = 2L, title = "Kosong", subTasks = null)
        assertTrue(taskEmptySubTasks.subTaskList.isEmpty())
    }

    @Test
    fun `tasks sorting prioritizes incomplete tasks before completed tasks`() = runTest {
        val task1 = TaskEntity(id = 1L, title = "Task Selesai", isDone = true, priority = 0)
        val task2 = TaskEntity(id = 2L, title = "Task Belum", isDone = false, priority = 2)

        taskRepository.insertTask(task1)
        taskRepository.insertTask(task2)

        val tasks = taskRepository.getAllTasks().first()
        assertEquals(2, tasks.size)
        assertFalse(tasks[0].isDone) // Incomplete first
        assertTrue(tasks[1].isDone)  // Completed second
    }
}
