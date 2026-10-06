package com.stockita.core.database

import com.stockita.core.database.entity.StockMovementEntity
import com.stockita.core.database.entity.TransactionEntity
import com.stockita.core.database.entity.TransactionItemEntity
import com.stockita.fakes.FakeCheckoutDao
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class CheckoutTransactionLogicTest {

    private lateinit var checkoutDao: FakeCheckoutDao

    @Before
    fun setUp() {
        checkoutDao = FakeCheckoutDao()
    }

    @Test
    fun `checkout transaction binds foreign keys refId and transactionId correctly and adjusts stocks`() = runTest {
        // Initial stocks
        val productId = 10L
        val materialId = 20L
        checkoutDao.productStocks[productId] = 50.0
        checkoutDao.materialStocks[materialId] = 200.0

        val trx = TransactionEntity(
            id = 0L,
            createdAt = 1700000000L,
            subtotal = 30000L,
            discount = 0L,
            total = 30000L,
            paymentMethod = "CASH",
            note = "Meja 1"
        )

        val items = listOf(
            TransactionItemEntity(
                id = 0L,
                transactionId = 0L, // will be bound
                productId = productId,
                nameSnapshot = "Produk A",
                priceSnapshot = 30000L,
                hppSnapshot = 10000L,
                qty = 2.0
            )
        )

        val movements = listOf(
            StockMovementEntity(
                id = 0L,
                targetType = "PRODUCT",
                targetId = productId,
                qty = -2.0,
                reason = "SALE",
                refId = null // will be bound
            ),
            StockMovementEntity(
                id = 0L,
                targetType = "MATERIAL",
                targetId = materialId,
                qty = -10.0,
                reason = "SALE",
                refId = null // will be bound
            )
        )

        checkoutDao.checkout(trx, items, movements)

        // 1. Transaction was assigned an ID
        val insertedTrx = checkoutDao.lastInsertedTransaction
        assertNotNull(insertedTrx)
        val generatedId = insertedTrx!!.id
        assertEquals(1L, generatedId)

        // 2. All items got the generated transactionId
        assertEquals(1, checkoutDao.insertedItems.size)
        assertEquals(generatedId, checkoutDao.insertedItems[0].transactionId)

        // 3. All movements got the generated refId
        assertEquals(2, checkoutDao.insertedMovements.size)
        assertEquals(generatedId, checkoutDao.insertedMovements[0].refId)
        assertEquals(generatedId, checkoutDao.insertedMovements[1].refId)

        // 4. Product stock updated: 50.0 + (-2.0) = 48.0
        assertEquals(48.0, checkoutDao.productStocks[productId]!!, 0.001)

        // 5. Material stock updated: 200.0 + (-10.0) = 190.0
        assertEquals(190.0, checkoutDao.materialStocks[materialId]!!, 0.001)
    }
}
