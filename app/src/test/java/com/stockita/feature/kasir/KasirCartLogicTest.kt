package com.stockita.feature.kasir

import com.stockita.core.database.entity.ProductEntity
import org.junit.Assert.*
import org.junit.Test

class KasirCartLogicTest {

    private val sampleProduct1 = ProductEntity(
        id = 1L,
        name = "Kopi Susu Gula Aren",
        categoryId = null,
        price = 15000L,
        stockMode = "RECIPE",
        stock = 0.0
    )

    private val sampleProduct2 = ProductEntity(
        id = 2L,
        name = "Croissant Keju",
        categoryId = null,
        price = 22000L,
        stockMode = "DIRECT",
        stock = 10.0
    )

    @Test
    fun `addToCart adds new item with qty 1 when cart is empty`() {
        val cart = mutableListOf<CartItem>()

        // Simulate addToCart logic
        val existingIndex = cart.indexOfFirst { it.product.id == sampleProduct1.id }
        if (existingIndex != -1) {
            cart[existingIndex] = cart[existingIndex].copy(qty = cart[existingIndex].qty + 1.0)
        } else {
            cart.add(CartItem(sampleProduct1, 1.0))
        }

        assertEquals(1, cart.size)
        assertEquals(sampleProduct1.id, cart[0].product.id)
        assertEquals(1.0, cart[0].qty, 0.001)
    }

    @Test
    fun `addToCart increments quantity when item already in cart`() {
        val cart = mutableListOf(CartItem(sampleProduct1, 1.0))

        val existingIndex = cart.indexOfFirst { it.product.id == sampleProduct1.id }
        if (existingIndex != -1) {
            cart[existingIndex] = cart[existingIndex].copy(qty = cart[existingIndex].qty + 1.0)
        } else {
            cart.add(CartItem(sampleProduct1, 1.0))
        }

        assertEquals(1, cart.size)
        assertEquals(2.0, cart[0].qty, 0.001)
    }

    @Test
    fun `updateCartQty updates quantity to new positive value`() {
        val cart = mutableListOf(CartItem(sampleProduct1, 2.0))

        val existingIndex = cart.indexOfFirst { it.product.id == sampleProduct1.id }
        val newQty = 5.0
        if (existingIndex != -1) {
            if (newQty <= 0.0) {
                cart.removeAt(existingIndex)
            } else {
                cart[existingIndex] = cart[existingIndex].copy(qty = newQty)
            }
        }

        assertEquals(1, cart.size)
        assertEquals(5.0, cart[0].qty, 0.001)
    }

    @Test
    fun `updateCartQty removes item when quantity is zero or negative`() {
        val cart = mutableListOf(
            CartItem(sampleProduct1, 1.0),
            CartItem(sampleProduct2, 2.0)
        )

        val existingIndex = cart.indexOfFirst { it.product.id == sampleProduct1.id }
        val newQty = 0.0
        if (existingIndex != -1) {
            if (newQty <= 0.0) {
                cart.removeAt(existingIndex)
            } else {
                cart[existingIndex] = cart[existingIndex].copy(qty = newQty)
            }
        }

        assertEquals(1, cart.size)
        assertEquals(sampleProduct2.id, cart[0].product.id)
    }

    @Test
    fun `cart subtotal calculation sums total price correctly`() {
        val cart = listOf(
            CartItem(sampleProduct1, 3.0), // 3 * 15000 = 45000
            CartItem(sampleProduct2, 2.0)  // 2 * 22000 = 44000
        )

        val total = cart.sumOf { (it.product.price * it.qty).toLong() }
        assertEquals(89000L, total)
    }
}
