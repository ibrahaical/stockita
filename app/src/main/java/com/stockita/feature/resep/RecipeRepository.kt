package com.stockita.feature.resep

import com.stockita.core.database.dao.RecipeDao
import com.stockita.core.database.entity.RecipeItemEntity
import com.stockita.core.database.model.RecipeItemWithMaterial
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao
) {
    fun getRecipeForProduct(productId: Long): Flow<List<RecipeItemWithMaterial>> {
        return recipeDao.getRecipeForProduct(productId)
    }

    suspend fun addRecipeItem(productId: Long, materialId: Long, qty: Double) {
        recipeDao.insertRecipeItem(
            RecipeItemEntity(
                productId = productId,
                materialId = materialId,
                qty = qty
            )
        )
    }

    suspend fun removeRecipeItem(item: RecipeItemEntity) {
        recipeDao.deleteRecipeItem(item)
    }
}
