package com.stockita.core.database.model

data class RecipeItemWithMaterial(
    val recipeItemId: Long,
    val productId: Long,
    val materialId: Long,
    val qty: Double,
    val materialName: String,
    val unit: String,
    val lastCost: Long
)
