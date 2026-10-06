package com.stockita.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stockita.core.database.entity.RecipeItemEntity
import com.stockita.core.database.model.RecipeItemWithMaterial
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("""
        SELECT r.id AS recipeItemId, r.productId, r.materialId, r.qty,
               m.name AS materialName, m.unit, m.lastCost
        FROM recipe_items r
        INNER JOIN materials m ON r.materialId = m.id
        WHERE r.productId = :productId
    """)
    fun getRecipeForProduct(productId: Long): Flow<List<RecipeItemWithMaterial>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipeItem(item: RecipeItemEntity): Long

    @Delete
    suspend fun deleteRecipeItem(item: RecipeItemEntity)
}
