package com.stockita.feature.resep

import com.stockita.core.database.model.RecipeItemWithMaterial
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HitungHppUseCase @Inject constructor() {
    
    /**
     * Menghitung Harga Pokok Penjualan (HPP) berdasarkan list bahan baku pada resep.
     * lastCost diasumsikan sebagai harga per satuan unit.
     */
    operator fun invoke(recipeItems: List<RecipeItemWithMaterial>): Long {
        var totalHpp = 0.0
        for (item in recipeItems) {
            // qty * lastCost
            totalHpp += (item.qty * item.lastCost)
        }
        return totalHpp.toLong()
    }
}
