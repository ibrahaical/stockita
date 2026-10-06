package com.stockita.feature.tugas

import com.stockita.core.database.entity.TaskEntity
import com.stockita.fakes.FakeTaskDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

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
    fun `tasks sorting prioritizes incomplete tasks before completed tasks`() = runTest {
        val task1 = TaskEntity(id = 1L, title = "Task Selesai", isDone = true)
        val task2 = TaskEntity(id = 2L, title = "Task Belum", isDone = false)

        taskRepository.insertTask(task1)
        taskRepository.insertTask(task2)

        val tasks = taskRepository.getAllTasks().first()
        assertEquals(2, tasks.size)
        assertFalse(tasks[0].isDone) // Incomplete first
        assertTrue(tasks[1].isDone)  // Completed second
    }
}
