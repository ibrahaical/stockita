package com.stockita.feature.produk

import com.stockita.core.database.entity.ProductEntity
import com.stockita.fakes.FakeProductDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProdukLogicTest {

    private lateinit var fakeProductDao: FakeProductDao
    private lateinit var productRepository: ProductRepository

    @Before
    fun setUp() {
        fakeProductDao = FakeProductDao()
        productRepository = ProductRepository(fakeProductDao)
    }

    @Test
    fun `insertProduct stores product and sorts by name`() = runTest {
        val p1 = ProductEntity(id = 0L, name = "Kopi Susu", categoryId = null, price = 15000L, stockMode = "RECIPE", stock = 0.0)
        val p2 = ProductEntity(id = 0L, name = "Americano", categoryId = null, price = 12000L, stockMode = "RECIPE", stock = 0.0)

        productRepository.insertProduct(p1)
        productRepository.insertProduct(p2)

        val products = productRepository.getAllProducts().first()
        assertEquals(2, products.size)
        // Check alphabetical sorting
        assertEquals("Americano", products[0].name)
        assertEquals("Kopi Susu", products[1].name)
    }

    @Test
    fun `updateProduct updates product price and stockMode`() = runTest {
        val initial = ProductEntity(id = 1L, name = "Roti Cokelat", categoryId = null, price = 8000L, stockMode = "DIRECT", stock = 10.0)
        productRepository.insertProduct(initial)

        val updated = initial.copy(price = 9000L, stock = 15.0)
        productRepository.updateProduct(updated)

        val products = productRepository.getAllProducts().first()
        assertEquals(1, products.size)
        assertEquals(9000L, products[0].price)
        assertEquals(15.0, products[0].stock, 0.001)
    }
}
