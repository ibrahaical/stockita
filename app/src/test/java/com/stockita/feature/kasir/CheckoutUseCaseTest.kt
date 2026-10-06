package com.stockita.feature.kasir

import com.stockita.core.database.entity.MaterialEntity
import com.stockita.core.database.entity.ProductEntity
import com.stockita.core.database.entity.RecipeItemEntity
import com.stockita.fakes.FakeCheckoutDao
import com.stockita.fakes.FakeRecipeDao
import com.stockita.feature.resep.RecipeRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CheckoutUseCaseTest {

    private lateinit var fakeCheckoutDao: FakeCheckoutDao
    private lateinit var fakeRecipeDao: FakeRecipeDao
    private lateinit var kasirRepository: KasirRepository
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var checkoutUseCase: CheckoutUseCase

    @Before
    fun setUp() {
        fakeCheckoutDao = FakeCheckoutDao()
        fakeRecipeDao = FakeRecipeDao()
        kasirRepository = KasirRepository(fakeCheckoutDao)
        recipeRepository = RecipeRepository(fakeRecipeDao)
        checkoutUseCase = CheckoutUseCase(kasirRepository, recipeRepository)
    }

    @Test
    fun `checkout with empty cart does nothing`() = runTest {
        checkoutUseCase(emptyList(), "CASH", "Meja 1")

        assertNull(fakeCheckoutDao.lastInsertedTransaction)
        assertTrue(fakeCheckoutDao.insertedItems.isEmpty())
        assertTrue(fakeCheckoutDao.insertedMovements.isEmpty())
    }

    @Test
    fun `checkout with DIRECT stockMode product deducts product stock directly`() = runTest {
        val product = ProductEntity(
            id = 10L,
            name = "Air Mineral Botol",
            categoryId = null,
            price = 5000L,
            stockMode = "DIRECT",
            stock = 20.0
        )
        fakeCheckoutDao.productStocks[product.id] = 20.0

        val cart = listOf(CartItem(product = product, qty = 3.0))

        checkoutUseCase(cart, "CASH", "Takeaway")

        // 1. Verify transaction
        val trx = fakeCheckoutDao.lastInsertedTransaction
        assertNotNull(trx)
        assertEquals(15000L, trx!!.subtotal)
        assertEquals(15000L, trx.total)
        assertEquals("CASH", trx.paymentMethod)
        assertEquals("Takeaway", trx.note)

        // 2. Verify transaction items
        assertEquals(1, fakeCheckoutDao.insertedItems.size)
        val item = fakeCheckoutDao.insertedItems[0]
        assertEquals(product.id, item.productId)
        assertEquals("Air Mineral Botol", item.nameSnapshot)
        assertEquals(5000L, item.priceSnapshot)
        assertEquals(3.0, item.qty, 0.001)
        assertEquals(0L, item.hppSnapshot)

        // 3. Verify stock movement
        assertEquals(1, fakeCheckoutDao.insertedMovements.size)
        val movement = fakeCheckoutDao.insertedMovements[0]
        assertEquals("PRODUCT", movement.targetType)
        assertEquals(product.id, movement.targetId)
        assertEquals(-3.0, movement.qty, 0.001)
        assertEquals("SALE", movement.reason)

        // 4. Verify product stock in dao was adjusted
        assertEquals(17.0, fakeCheckoutDao.productStocks[product.id]!!, 0.001)
    }

    @Test
    fun `checkout with RECIPE stockMode product calculates HPP and deducts material stocks`() = runTest {
        val kopiSusu = ProductEntity(
            id = 20L,
            name = "Es Kopi Susu",
            categoryId = null,
            price = 18000L,
            stockMode = "RECIPE",
            stock = 0.0
        )

        val materialKopi = MaterialEntity(
            id = 101L,
            name = "Kopi Espresso",
            unit = "shot",
            stock = 50.0,
            minStock = 5.0,
            lastCost = 3000L,
            categoryId = null
        )
        val materialSusu = MaterialEntity(
            id = 102L,
            name = "Fresh Milk",
            unit = "ml",
            stock = 1000.0,
            minStock = 200.0,
            lastCost = 25L, // 100ml * 25 = 2500
            categoryId = null
        )

        fakeCheckoutDao.materialStocks[materialKopi.id] = 50.0
        fakeCheckoutDao.materialStocks[materialSusu.id] = 1000.0

        fakeRecipeDao.materialsProvider = { listOf(materialKopi, materialSusu) }
        fakeRecipeDao.insertRecipeItem(RecipeItemEntity(productId = 20L, materialId = 101L, qty = 1.0))
        fakeRecipeDao.insertRecipeItem(RecipeItemEntity(productId = 20L, materialId = 102L, qty = 100.0))

        // Order 2 cups of Es Kopi Susu
        val cart = listOf(CartItem(product = kopiSusu, qty = 2.0))

        checkoutUseCase(cart, "QRIS", "Dine-in Meja 5")

        // 1. Transaction verification: 2 * 18000 = 36000
        val trx = fakeCheckoutDao.lastInsertedTransaction
        assertNotNull(trx)
        assertEquals(36000L, trx!!.total)
        assertEquals("QRIS", trx.paymentMethod)

        // 2. Transaction Item snapshot verification
        assertEquals(1, fakeCheckoutDao.insertedItems.size)
        val item = fakeCheckoutDao.insertedItems[0]
        assertEquals(kopiSusu.id, item.productId)
        // HPP per cup = (1.0 * 3000) + (100.0 * 25) = 3000 + 2500 = 5500
        assertEquals(5500L, item.hppSnapshot)
        assertEquals(2.0, item.qty, 0.001)

        // 3. Stock Movements: for 2 cups ->
        // materialKopi: - (1.0 * 2.0) = -2.0
        // materialSusu: - (100.0 * 2.0) = -200.0
        assertEquals(2, fakeCheckoutDao.insertedMovements.size)
        val movKopi = fakeCheckoutDao.insertedMovements.first { it.targetId == 101L }
        val movSusu = fakeCheckoutDao.insertedMovements.first { it.targetId == 102L }

        assertEquals("MATERIAL", movKopi.targetType)
        assertEquals(-2.0, movKopi.qty, 0.001)
        assertEquals("SALE", movKopi.reason)

        assertEquals("MATERIAL", movSusu.targetType)
        assertEquals(-200.0, movSusu.qty, 0.001)
        assertEquals("SALE", movSusu.reason)

        // 4. Material stocks reduced:
        // 50.0 - 2.0 = 48.0
        // 1000.0 - 200.0 = 800.0
        assertEquals(48.0, fakeCheckoutDao.materialStocks[101L]!!, 0.001)
        assertEquals(800.0, fakeCheckoutDao.materialStocks[102L]!!, 0.001)
    }

    @Test
    fun `checkout with NONE stockMode product does not deduct any stock`() = runTest {
        val jasaBungkus = ProductEntity(
            id = 30L,
            name = "Jasa Sablon / Print",
            categoryId = null,
            price = 10000L,
            stockMode = "NONE",
            stock = 0.0
        )

        val cart = listOf(CartItem(product = jasaBungkus, qty = 1.0))

        checkoutUseCase(cart, "TRANSFER", null)

        val trx = fakeCheckoutDao.lastInsertedTransaction
        assertNotNull(trx)
        assertEquals(10000L, trx!!.total)

        assertEquals(1, fakeCheckoutDao.insertedItems.size)
        val item = fakeCheckoutDao.insertedItems[0]
        assertEquals(30L, item.productId)
        assertEquals(0L, item.hppSnapshot)

        // No movements should be created for NONE mode
        assertTrue(fakeCheckoutDao.insertedMovements.isEmpty())
    }

    @Test
    fun `checkout with mixed cart handles all stockModes correctly in single transaction`() = runTest {
        val pDirect = ProductEntity(id = 1L, name = "Snack Keripik", categoryId = null, price = 10000L, stockMode = "DIRECT", stock = 10.0)
        val pRecipe = ProductEntity(id = 2L, name = "Es Teh Manis", categoryId = null, price = 5000L, stockMode = "RECIPE", stock = 0.0)
        val pNone = ProductEntity(id = 3L, name = "Biaya Antar", categoryId = null, price = 2000L, stockMode = "NONE", stock = 0.0)

        val matTeh = MaterialEntity(id = 99L, name = "Kantong Teh", unit = "kantong", stock = 50.0, minStock = 10.0, lastCost = 500L, categoryId = null)
        fakeCheckoutDao.materialStocks[99L] = 50.0
        fakeCheckoutDao.productStocks[1L] = 10.0

        fakeRecipeDao.materialsProvider = { listOf(matTeh) }
        fakeRecipeDao.insertRecipeItem(RecipeItemEntity(productId = 2L, materialId = 99L, qty = 1.0))

        val cart = listOf(
            CartItem(pDirect, 2.0), // 2 * 10000 = 20000
            CartItem(pRecipe, 3.0), // 3 * 5000  = 15000
            CartItem(pNone, 1.0)    // 1 * 2000  = 2000
        )

        checkoutUseCase(cart, "CASH", "Paket Komplit")

        val trx = fakeCheckoutDao.lastInsertedTransaction
        assertNotNull(trx)
        // Subtotal = 20000 + 15000 + 2000 = 37000
        assertEquals(37000L, trx!!.total)

        assertEquals(3, fakeCheckoutDao.insertedItems.size)

        // Stock movements: 1 for pDirect (qty = -2), 1 for matTeh (qty = -3), 0 for pNone
        assertEquals(2, fakeCheckoutDao.insertedMovements.size)

        val movDirect = fakeCheckoutDao.insertedMovements.first { it.targetType == "PRODUCT" }
        assertEquals(1L, movDirect.targetId)
        assertEquals(-2.0, movDirect.qty, 0.001)

        val movRecipe = fakeCheckoutDao.insertedMovements.first { it.targetType == "MATERIAL" }
        assertEquals(99L, movRecipe.targetId)
        assertEquals(-3.0, movRecipe.qty, 0.001)

        // Stocks updated
        assertEquals(8.0, fakeCheckoutDao.productStocks[1L]!!, 0.001)
        assertEquals(47.0, fakeCheckoutDao.materialStocks[99L]!!, 0.001)
    }
}
