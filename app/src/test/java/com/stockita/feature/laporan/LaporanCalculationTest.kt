package com.stockita.feature.laporan

import com.stockita.core.database.entity.ExpenseEntity
import com.stockita.core.database.entity.ProductEntity
import com.stockita.core.database.entity.TransactionEntity
import com.stockita.core.database.entity.TransactionItemEntity
import com.stockita.fakes.FakeReportDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LaporanCalculationTest {

    private lateinit var transactions: MutableList<TransactionEntity>
    private lateinit var transactionItems: MutableList<TransactionItemEntity>
    private lateinit var expenses: MutableList<ExpenseEntity>
    private lateinit var products: MutableList<ProductEntity>
    private lateinit var fakeReportDao: FakeReportDao
    private lateinit var laporanRepository: LaporanRepository

    @Before
    fun setUp() {
        transactions = mutableListOf()
        transactionItems = mutableListOf()
        expenses = mutableListOf()
        products = mutableListOf()

        fakeReportDao = FakeReportDao(
            transactions = { transactions },
            transactionItems = { transactionItems },
            expenses = { expenses },
            products = { products }
        )
        laporanRepository = LaporanRepository(fakeReportDao)
    }

    @Test
    fun `getLabaRugiSummary calculates omzet, hpp, and expenses within date range`() = runTest {
        val rangeStart = 1000L
        val rangeEnd = 2000L

        // Inside range
        transactions.add(TransactionEntity(id = 1L, createdAt = 1500L, subtotal = 120000L, discount = 0L, total = 120000L, paymentMethod = "CASH"))
        transactionItems.add(TransactionItemEntity(id = 1L, transactionId = 1L, productId = 10L, nameSnapshot = "P1", priceSnapshot = 60000L, hppSnapshot = 25000L, qty = 2.0))
        expenses.add(ExpenseEntity(id = 1L, title = "Kantong Plastik", amount = 10000L, category = "Operasional", createdAt = 1600L))

        // Outside range (before)
        transactions.add(TransactionEntity(id = 2L, createdAt = 500L, subtotal = 80000L, discount = 0L, total = 80000L, paymentMethod = "CASH"))
        transactionItems.add(TransactionItemEntity(id = 2L, transactionId = 2L, productId = 10L, nameSnapshot = "P1", priceSnapshot = 80000L, hppSnapshot = 30000L, qty = 1.0))
        expenses.add(ExpenseEntity(id = 2L, title = "Listrik Lama", amount = 50000L, category = "Utilitas", createdAt = 500L))

        // Outside range (after)
        transactions.add(TransactionEntity(id = 3L, createdAt = 2500L, subtotal = 90000L, discount = 0L, total = 90000L, paymentMethod = "CASH"))

        val result = laporanRepository.getLabaRugiSummary(rangeStart, rangeEnd).first()

        // Omzet should only be 120,000
        assertEquals(120000L, result.omzet)
        // HPP should only be 25,000 * 2 = 50,000
        assertEquals(50000L, result.hpp)
        // Expenses should only be 10,000
        assertEquals(10000L, result.expenses)

        // Net Profit = 120,000 - 50,000 - 10,000 = 60,000
        val labaBersih = (result.omzet ?: 0L) - (result.hpp ?: 0L) - (result.expenses ?: 0L)
        assertEquals(60000L, labaBersih)
    }

    @Test
    fun `getTopProducts ranks products by total quantity sold within date range`() = runTest {
        val rangeStart = 1000L
        val rangeEnd = 2000L

        products.add(ProductEntity(id = 101L, name = "Kopi Susu", categoryId = null, price = 15000L, stockMode = "DIRECT", stock = 100.0))
        products.add(ProductEntity(id = 102L, name = "Roti Bakar", categoryId = null, price = 20000L, stockMode = "DIRECT", stock = 50.0))

        transactions.add(TransactionEntity(id = 1L, createdAt = 1500L, subtotal = 55000L, discount = 0L, total = 55000L, paymentMethod = "CASH"))
        transactionItems.add(TransactionItemEntity(id = 1L, transactionId = 1L, productId = 101L, nameSnapshot = "Kopi Susu", priceSnapshot = 15000L, hppSnapshot = 5000L, qty = 5.0))
        transactionItems.add(TransactionItemEntity(id = 2L, transactionId = 1L, productId = 102L, nameSnapshot = "Roti Bakar", priceSnapshot = 20000L, hppSnapshot = 8000L, qty = 2.0))

        val topProducts = laporanRepository.getTopProducts(rangeStart, rangeEnd).first()

        assertEquals(2, topProducts.size)
        // Kopi Susu has higher quantity (5.0 vs 2.0)
        assertEquals("Kopi Susu", topProducts[0].itemName)
        assertEquals(5.0, topProducts[0].totalQty, 0.001)
        assertEquals(75000L, topProducts[0].totalRevenue)

        assertEquals("Roti Bakar", topProducts[1].itemName)
        assertEquals(2.0, topProducts[1].totalQty, 0.001)
        assertEquals(40000L, topProducts[1].totalRevenue)
    }
}
