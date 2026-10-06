package com.stockita.feature.stok

import com.stockita.core.database.entity.MaterialEntity
import com.stockita.core.database.entity.ProductEntity
import com.stockita.fakes.FakeMaterialDao
import com.stockita.fakes.FakeStockDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class StokLogicTest {

    private lateinit var fakeMaterialDao: FakeMaterialDao
    private lateinit var fakeStockDao: FakeStockDao
    private lateinit var materialRepository: MaterialRepository
    private lateinit var stockMovementRepository: StockMovementRepository

    @Before
    fun setUp() {
        fakeMaterialDao = FakeMaterialDao()
        fakeStockDao = FakeStockDao()
        materialRepository = MaterialRepository(fakeMaterialDao)
        stockMovementRepository = StockMovementRepository(fakeStockDao)
    }

    @Test
    fun `insertMaterial stores new material correctly`() = runTest {
        val material = MaterialEntity(
            id = 0L,
            name = "Biji Kopi Arabika",
            unit = "gram",
            stock = 1000.0,
            minStock = 200.0,
            lastCost = 300L,
            categoryId = null
        )

        materialRepository.insertMaterial(material)

        val materials = materialRepository.getAllMaterials().first()
        assertEquals(1, materials.size)
        assertEquals("Biji Kopi Arabika", materials[0].name)
        assertEquals(1000.0, materials[0].stock, 0.001)
        assertEquals(200.0, materials[0].minStock, 0.001)
    }

    @Test
    fun `updateMaterial modifies existing material properties`() = runTest {
        val initial = MaterialEntity(
            id = 1L,
            name = "Susu Kental Manis",
            unit = "kaleng",
            stock = 10.0,
            minStock = 2.0,
            lastCost = 12000L,
            categoryId = null
        )
        materialRepository.insertMaterial(initial)

        val updated = initial.copy(stock = 15.0, lastCost = 13000L)
        materialRepository.updateMaterial(updated)

        val materials = materialRepository.getAllMaterials().first()
        assertEquals(1, materials.size)
        assertEquals(15.0, materials[0].stock, 0.001)
        assertEquals(13000L, materials[0].lastCost)
    }

    @Test
    fun `adjustStock for MATERIAL updates material balance and logs movement`() = runTest {
        val materialId = 10L
        fakeStockDao.materialStocks[materialId] = 50.0

        // Purchase +20 units
        stockMovementRepository.adjustStock(
            targetType = "MATERIAL",
            targetId = materialId,
            qty = 20.0,
            reason = "PURCHASE"
        )

        assertEquals(70.0, fakeStockDao.materialStocks[materialId]!!, 0.001)

        val movements = stockMovementRepository.getAllMovements().first()
        assertEquals(1, movements.size)
        assertEquals("MATERIAL", movements[0].targetType)
        assertEquals(materialId, movements[0].targetId)
        assertEquals(20.0, movements[0].qty, 0.001)
        assertEquals("PURCHASE", movements[0].reason)
    }

    @Test
    fun `adjustStock for MATERIAL with WASTE reduces balance and logs correctly`() = runTest {
        val materialId = 10L
        fakeStockDao.materialStocks[materialId] = 50.0

        // Waste -5 units (e.g. spilled or expired)
        stockMovementRepository.adjustStock(
            targetType = "MATERIAL",
            targetId = materialId,
            qty = -5.0,
            reason = "WASTE"
        )

        assertEquals(45.0, fakeStockDao.materialStocks[materialId]!!, 0.001)

        val movements = stockMovementRepository.getAllMovements().first()
        assertEquals(1, movements.size)
        assertEquals(-5.0, movements[0].qty, 0.001)
        assertEquals("WASTE", movements[0].reason)
    }

    @Test
    fun `adjustStock for PRODUCT updates product stock balance`() = runTest {
        val productId = 5L
        fakeStockDao.productStocks[productId] = 100.0

        // Adjust stock by +15
        stockMovementRepository.adjustStock(
            targetType = "PRODUCT",
            targetId = productId,
            qty = 15.0,
            reason = "ADJUST"
        )

        assertEquals(115.0, fakeStockDao.productStocks[productId]!!, 0.001)

        val movements = stockMovementRepository.getAllMovements().first()
        assertEquals(1, movements.size)
        assertEquals("PRODUCT", movements[0].targetType)
        assertEquals(productId, movements[0].targetId)
        assertEquals(15.0, movements[0].qty, 0.001)
        assertEquals("ADJUST", movements[0].reason)
    }

    @Test
    fun `low stock condition correctly flags materials when stock is less or equal minStock`() {
        val safeMaterial = MaterialEntity(
            id = 1L, name = "Gula", unit = "kg", stock = 10.0, minStock = 5.0, lastCost = 15000L, categoryId = null
        )
        val warningMaterial = MaterialEntity(
            id = 2L, name = "Kopi", unit = "kg", stock = 2.0, minStock = 5.0, lastCost = 90000L, categoryId = null
        )
        val exactMinMaterial = MaterialEntity(
            id = 3L, name = "Cup", unit = "pcs", stock = 50.0, minStock = 50.0, lastCost = 500L, categoryId = null
        )

        val materials = listOf(safeMaterial, warningMaterial, exactMinMaterial)

        val lowStock = materials.filter { it.stock <= it.minStock }

        assertEquals(2, lowStock.size)
        assertTrue(lowStock.any { it.name == "Kopi" })
        assertTrue(lowStock.any { it.name == "Cup" })
        assertFalse(lowStock.any { it.name == "Gula" })
    }
}
