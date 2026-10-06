package com.stockita.feature.resep

import com.stockita.core.database.model.RecipeItemWithMaterial
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class HitungHppUseCaseTest {

    private lateinit var hitungHppUseCase: HitungHppUseCase

    @Before
    fun setUp() {
        hitungHppUseCase = HitungHppUseCase()
    }

    @Test
    fun `invoke with empty recipe returns zero`() {
        val result = hitungHppUseCase(emptyList())
        assertEquals(0L, result)
    }

    @Test
    fun `invoke with single item calculates correct HPP`() {
        val items = listOf(
            RecipeItemWithMaterial(
                recipeItemId = 1L,
                productId = 100L,
                materialId = 10L,
                qty = 2.0,
                materialName = "Kopi Bubuk",
                unit = "gram",
                lastCost = 1500L
            )
        )

        // 2.0 * 1500 = 3000
        val result = hitungHppUseCase(items)
        assertEquals(3000L, result)
    }

    @Test
    fun `invoke with multiple items calculates sum of HPP accurately`() {
        val items = listOf(
            RecipeItemWithMaterial(
                recipeItemId = 1L,
                productId = 100L,
                materialId = 10L,
                qty = 20.0,
                materialName = "Kopi Bubuk",
                unit = "gram",
                lastCost = 250L // 20 * 250 = 5000
            ),
            RecipeItemWithMaterial(
                recipeItemId = 2L,
                productId = 100L,
                materialId = 11L,
                qty = 150.0,
                materialName = "Susu UHT",
                unit = "ml",
                lastCost = 20L // 150 * 20 = 3000
            ),
            RecipeItemWithMaterial(
                recipeItemId = 3L,
                productId = 100L,
                materialId = 12L,
                qty = 1.0,
                materialName = "Cup & Sedotan",
                unit = "pcs",
                lastCost = 1200L // 1 * 1200 = 1200
            )
        )

        // Total = 5000 + 3000 + 1200 = 9200
        val result = hitungHppUseCase(items)
        assertEquals(9200L, result)
    }

    @Test
    fun `invoke with fractional quantities computes exact floating point multiplication`() {
        val items = listOf(
            RecipeItemWithMaterial(
                recipeItemId = 1L,
                productId = 101L,
                materialId = 20L,
                qty = 0.25, // 1/4 kg
                materialName = "Tepung Terigu",
                unit = "kg",
                lastCost = 16000L // 0.25 * 16000 = 4000
            ),
            RecipeItemWithMaterial(
                recipeItemId = 2L,
                productId = 101L,
                materialId = 21L,
                qty = 0.5, // 1/2 kg
                materialName = "Gula Pasir",
                unit = "kg",
                lastCost = 18000L // 0.5 * 18000 = 9000
            )
        )

        // Total = 4000 + 9000 = 13000
        val result = hitungHppUseCase(items)
        assertEquals(13000L, result)
    }

    @Test
    fun `invoke with zero cost or zero quantity returns zero total`() {
        val items = listOf(
            RecipeItemWithMaterial(
                recipeItemId = 1L,
                productId = 102L,
                materialId = 30L,
                qty = 0.0,
                materialName = "Air Galon",
                unit = "ml",
                lastCost = 50L
            ),
            RecipeItemWithMaterial(
                recipeItemId = 2L,
                productId = 102L,
                materialId = 31L,
                qty = 100.0,
                materialName = "Es Batu (Gratis)",
                unit = "cube",
                lastCost = 0L
            )
        )

        val result = hitungHppUseCase(items)
        assertEquals(0L, result)
    }
}
